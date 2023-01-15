package jig.erd.domain.primitive;

public record ColumnIdentifier(String schemaName, String entityName, String columnName) {

    public ColumnIdentifier(Entity entity, String name) {
        this(entity.schema().name(), entity.name(), name);
    }

    public EntityIdentifier toEntityIdentifier() {
        return new EntityIdentifier(schemaName, entityName);
    }
}
