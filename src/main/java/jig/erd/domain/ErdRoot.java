package jig.erd.domain;

import jig.erd.JigProperties;
import jig.erd.domain.diagram.detail.ColumnRelationDiagram;
import jig.erd.domain.diagram.detail.DetailEntities;
import jig.erd.domain.diagram.detail.DetailEntity;
import jig.erd.domain.diagram.detail.DetailSchema;
import jig.erd.domain.diagram.overview.SchemaRelationDiagram;
import jig.erd.domain.diagram.summary.EntityRelationDiagram;
import jig.erd.domain.diagram.summary.SummarySchema;
import jig.erd.domain.primitive.*;

import java.util.List;
import java.util.logging.Logger;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

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

    public ColumnRelationDiagram columnRelationDiagram() {
        Columns allColumns = new Columns(this.columns);

        DetailEntities allEntities = entities.stream()
                .map(entity -> new DetailEntity(entity, allColumns.only(entity)))
                .collect(collectingAndThen(toList(), DetailEntities::new));

        List<DetailSchema> detailSchemas = schemas.stream()
                .map(schema -> new DetailSchema(schema, allEntities.only(schema)))
                .collect(toList());

        return new ColumnRelationDiagram(detailSchemas, columnRelations());
    }

    public SchemaRelationDiagram schemaRelationDiagram() {
        return new SchemaRelationDiagram(schemas, columnRelations().toEntityRelations().toSchemaRelations());
    }

    public EntityRelationDiagram entityRelationDiagram() {
        List<SummarySchema> summarySchemas = schemas.stream()
                .map(schema -> new SummarySchema(schema, entities(schema)))
                .collect(toList());

        EntityRelations entityRelations = columnRelations().toEntityRelations();

        return new EntityRelationDiagram(summarySchemas, entityRelations);
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
