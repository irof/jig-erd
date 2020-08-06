package jig.erd.application.repository;

import jig.erd.domain.composite.CompositeEntities;
import jig.erd.domain.composite.CompositeEntity;
import jig.erd.domain.composite.CompositeSchema;
import jig.erd.domain.diagram.ColumnRelationDiagram;
import jig.erd.domain.primitive.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class Repository {

    List<Schema> schemas = new ArrayList<>();
    List<Entity> entities = new ArrayList<>();
    List<Column> columns = new ArrayList<>();
    List<ColumnRelation> columnRelations = new ArrayList<>();

    public void registerRelation(ColumnIdentifier fromColumn, ColumnIdentifier toColumn) {
        ColumnRelation columnRelation = new ColumnRelation(getColumn(fromColumn), getColumn(toColumn));
        columnRelations.add(columnRelation);
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
        entities.add(new Entity(schema, entityName, entityAlias));
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

        CompositeEntities allEntities = entities.stream()
                .map(entity -> new CompositeEntity(entity, allColumns.only(entity)))
                .collect(collectingAndThen(toList(), CompositeEntities::new));

        List<CompositeSchema> compositeSchemas = schemas.stream()
                .map(schema -> new CompositeSchema(schema, allEntities.only(schema)))
                .collect(toList());

        return new ColumnRelationDiagram(compositeSchemas, columnRelations());
    }

    private ColumnRelations columnRelations() {
        return new ColumnRelations(columnRelations);
    }
}
