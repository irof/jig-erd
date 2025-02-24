package jig.erd.infrastructure.database;

import jig.erd.domain.ErdRoot;
import jig.erd.domain.primitive.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

public class Repository {
    static final Logger logger = Logger.getLogger(Repository.class.getName());

    List<Schema> schemas = new ArrayList<>();
    List<Entity> entities = new ArrayList<>();
    List<Column> columns = new ArrayList<>();
    List<ColumnRelation> columnRelations = new ArrayList<>();

    /**
     * テーブルを登録する
     * @param schema スキーマ
     * @param entityName テーブル
     * @param entityAlias テーブル別名
     */
    public void registerEntity(Schema schema, String entityName, String entityAlias) {
        Entity entity = new Entity(schema, entityName, entityAlias);
        entities.add(entity);
        logger.fine("entity found:" + entity.readableLabel());
    }

    /**
     * 関連を登録する
     * @param fromColumn 元カラム名
     * @param toColumn 先カラム名
     */
    public void registerRelation(ColumnIdentifier fromColumn, ColumnIdentifier toColumn) {
        ColumnRelation columnRelation = new ColumnRelation(getColumn(fromColumn), getColumn(toColumn));
        columnRelations.add(columnRelation);
        logger.fine("relation found: " + columnRelation.readableLabel());
    }

    private Column getColumn(ColumnIdentifier columnIdentifier) {
        for (Column column : columns) {
            if (column.matches(columnIdentifier)) {
                return column;
            }
        }

        String refId = "r" + columns.size();
        Entity entity = getEntity(columnIdentifier.toEntityIdentifier());
        Column column = Column.generateWithoutAlias(columnIdentifier, refId, entity);
        columns.add(column);
        return column;
    }

    private Entity getEntity(EntityIdentifier entityIdentifier) {
        for (Entity entity : entities) {
            if (entity.matches(entityIdentifier)) {
                return entity;
            }
        }
        throw new NoSuchElementException(entityIdentifier.toString());
    }

    public Schema getSchema(String schemaName) {
        for (Schema schema : schemas) {
            if (schema.name().equals(schemaName)) return schema;
        }
        Schema schema = new Schema(schemaName);
        schemas.add(schema);
        return schema;
    }

    public ErdRoot buildErdRoot() {
        return new ErdRoot(schemas, entities, columns, columnRelations);
    }
}
