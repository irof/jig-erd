package jig.erd.domain;

import jig.erd.JigProperties;
import jig.erd.domain.diagram.DetailEntities;
import jig.erd.domain.diagram.DetailEntity;
import jig.erd.domain.diagram.DetailSchema;
import jig.erd.domain.diagram.Digraph;
import jig.erd.domain.diagram.SummarySchema;
import jig.erd.domain.primitive.*;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class ErdRoot {
    static final Logger logger = Logger.getLogger(ErdRoot.class.getName());

    List<Schema> schemas;
    List<Entity> entities;
    List<Column> columns;
    List<ColumnRelation> columnRelations;

    public ErdRoot(List<Schema> schemas, List<Entity> entities, List<Column> columns, List<ColumnRelation> columnRelations) {
        this.schemas = schemas;
        this.entities = entities;
        this.columns = columns;
        this.columnRelations = columnRelations;
    }

    public Digraph schemaRelationDiagram() {
        return Digraph.schemaRelationDiagram(
                dotAttributes -> schemas.stream().map(schema -> schema.nodeText()).collect(Collectors.joining("\n")),
                columnRelations().toEntityRelations().toSchemaRelations()
        );
    }

    public Digraph entityRelationDiagram() {
        return Digraph.entityRelationDiagram(
                dotAttributes -> {
                    return schemas.stream()
                            .map(schema -> {
                                Entities allEntities = new Entities(entities);
                                SummarySchema summarySchema = new SummarySchema(schema, allEntities.only(schema));
                                return summarySchema.graphText(dotAttributes);
                            })
                            .collect(Collectors.joining("\n"));
                },
                columnRelations().toEntityRelations()
        );
    }

    public Digraph columnRelationDiagram() {
        return Digraph.columnRelationDiagram(
                dotAttributes -> {
                    Columns allColumns = new Columns(this.columns);
                    DetailEntities allEntities = entities.stream()
                            .map(entity -> new DetailEntity(entity, allColumns.only(entity)))
                            .collect(collectingAndThen(toList(), DetailEntities::new));
                    return schemas.stream()
                            .map(schema -> new DetailSchema(schema, allEntities.only(schema)))
                            .map(detailSchema -> detailSchema.graphText(dotAttributes)).collect(joining("\n"));
                },
                columnRelations()
        );
    }

    ColumnRelations columnRelations() {
        return new ColumnRelations(columnRelations);
    }

    public ErdRoot filter(JigProperties jigProperties) {
        return jigProperties.filterSchemaPattern().map(
                pattern -> {
                    logger.info("loaded schemas: " + schemas);
                    logger.info("filter schema pattern: " + pattern);
                    List<Schema> filteredSchemas = this.schemas.stream().filter(schema -> schema.matchRegex(pattern)).collect(toList());
                    if (filteredSchemas.isEmpty()) {
                        logger.warning("パターンに合致するスキーマが存在しませんでした。全スキーマで出力します。");
                        return null;
                    }
                    return new ErdRoot(
                            filteredSchemas,
                            entities.stream().filter(entity -> entity.matchesSchema(filteredSchemas)).collect(toList()),
                            columns.stream().filter(column -> column.matchesSchema(filteredSchemas)).collect(toList()),
                            columnRelations.stream().filter(columnRelation -> columnRelation.bothMatchSchema(filteredSchemas)).collect(toList())
                    );
                }
        ).orElse(this);
    }

    public Summary summary() {
        return new Summary(schemas.size(), entities.size(), columns.size(), columnRelations.size());
    }
}
