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
        return new Digraph(
                jigProperties -> "node[shape=box,style=filled,fillcolor=lightyellow];",
                // nodes
                jigProperties -> schemas.stream().map(schema -> schema.nodeText()).collect(Collectors.joining("\n")),
                // edges
                jigProperties -> columnRelations().toEntityRelations().toSchemaRelations().edgesText()
        );
    }

    public Digraph entityRelationDiagram() {
        return new Digraph(
                jigProperties -> "graph[style=filled,fillcolor=lightyellow];",
                jigProperties -> "node[shape=box,style=filled,fillcolor=lightgoldenrod];",
                // nodes
                jigProperties -> {
                    return schemas.stream()
                            .map(schema -> new SummarySchema(schema, entities(schema)))
                            .map(summarySchema -> summarySchema.graphText())
                            .collect(Collectors.joining("\n"));
                },
                // edges
                jigProperties -> columnRelations().toEntityRelations().edgesText());
    }

    public Digraph columnRelationDiagram() {
        return new Digraph(
                jigProperties -> "graph[style=filled,fillcolor=lightyellow];",
                // labelにtableで書き出すのでshapeしない
                jigProperties -> "node[shape=plain];",
                // nodes
                jigProperties -> {
                    Columns allColumns = new Columns(this.columns);
                    DetailEntities allEntities = entities.stream()
                            .map(entity -> new DetailEntity(entity, allColumns.only(entity)))
                            .collect(collectingAndThen(toList(), DetailEntities::new));
                    return schemas.stream()
                            .map(schema -> new DetailSchema(schema, allEntities.only(schema)))
                            .map(detailSchema -> detailSchema.graphText()).collect(joining("\n"));
                },
                // edges
                jigProperties -> columnRelations().edgesText()
        );
    }

    ColumnRelations columnRelations() {
        return new ColumnRelations(columnRelations);
    }

    Entities entities(Schema schema) {
        Entities allEntities = new Entities(entities);
        return allEntities.only(schema);
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

    public String summaryText() {
        return String.format("schemas: %d, entities: %d, columns: %d, columnRelations: %d",
                schemas.size(), entities.size(), columns.size(), columnRelations.size());
    }
}
