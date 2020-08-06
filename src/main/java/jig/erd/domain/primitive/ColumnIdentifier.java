package jig.erd.domain.primitive;

public class ColumnIdentifier {
    String schemaName;
    String entityName;
    String columnName;

    public ColumnIdentifier(String schemaName, String entityName, String columnName) {
        this.schemaName = schemaName;
        this.entityName = entityName;
        this.columnName = columnName;
    }

    public EntityIdentifier toEntityIdentifier() {
        return new EntityIdentifier(schemaName, entityName);
    }
}
