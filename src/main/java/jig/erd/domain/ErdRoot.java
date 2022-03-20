package jig.erd.domain;

import jig.erd.domain.diagram.detail.ColumnRelationDiagram;
import jig.erd.domain.diagram.detail.DetailEntities;
import jig.erd.domain.diagram.detail.DetailEntity;
import jig.erd.domain.diagram.detail.DetailSchema;
import jig.erd.domain.diagram.overview.SchemaRelationDiagram;
import jig.erd.domain.diagram.summary.EntityRelationDiagram;
import jig.erd.domain.diagram.summary.SummarySchema;
import jig.erd.domain.primitive.*;

import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class ErdRoot {
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

    private ColumnRelations columnRelations() {
        return new ColumnRelations(columnRelations);
    }

    public EntityRelationDiagram entityRelationDiagram() {

        List<SummarySchema> summarySchemas = schemas.stream()
                .map(schema -> new SummarySchema(schema, entities(schema)))
                .collect(toList());

        EntityRelations entityRelations = columnRelations().toEntityRelations();

        return new EntityRelationDiagram(summarySchemas, entityRelations);
    }

    Entities entities(Schema schema) {
        Entities allEntities = new Entities(entities);
        return allEntities.only(schema);
    }

    public SchemaRelationDiagram schemaRelationDiagram() {
        return new SchemaRelationDiagram(schemas, columnRelations().toEntityRelations().toSchemaRelations());
    }
}
