package jig.erd.application.repository;

import jig.erd.domain.diagram.detail.ColumnRelationDiagram;
import jig.erd.domain.diagram.detail.DetailEntities;
import jig.erd.domain.diagram.detail.DetailEntity;
import jig.erd.domain.diagram.detail.DetailSchema;
import jig.erd.domain.diagram.overview.SchemaRelationDiagram;
import jig.erd.domain.diagram.summary.EntityRelationDiagram;
import jig.erd.domain.diagram.summary.SummarySchema;
import jig.erd.domain.primitive.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class Repository {
    static final Logger logger = Logger.getLogger(Repository.class.getName());

    List<Schema> schemas = new ArrayList<>();
    List<Entity> entities = new ArrayList<>();
    List<Column> columns = new ArrayList<>();
    List<ColumnRelation> columnRelations = new ArrayList<>();

    public void registerRelation(ColumnIdentifier fromColumn, ColumnIdentifier toColumn) {
        ColumnRelation columnRelation = new ColumnRelation(getColumn(fromColumn), getColumn(toColumn));
        columnRelations.add(columnRelation);
        logger.info("relation found: " + columnRelation.readableLabel());
    }

    public Column getColumn(ColumnIdentifier columnIdentifier) {
        for (Column column : columns) {
            if (column.matches(columnIdentifier)) {
                return column;
            }
        }

        String refId = "r" + columns.size();
        Entity entity = getEntity(columnIdentifier.toEntityIdentifier());
        Column column = Column.generate(columnIdentifier, refId, entity);
        columns.add(column);
        return column;
    }

    public Entity getEntity(EntityIdentifier entityIdentifier) {
        for (Entity entity : entities) {
            if (entity.matches(entityIdentifier)) {
                return entity;
            }
        }
        throw new NoSuchElementException(entityIdentifier.toString());
    }

    public void registerEntity(Schema schema, String entityName, String entityAlias) {
        Entity entity = new Entity(schema, entityName, entityAlias);
        entities.add(entity);
        logger.info("entity found:" + entity.readableLabel());
    }

    public Schema getSchema(String schemaName) {
        for (Schema schema : schemas) {
            if (schema.name().equals(schemaName)) return schema;
        }
        Schema schema = new Schema(schemaName);
        schemas.add(schema);
        return schema;
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
