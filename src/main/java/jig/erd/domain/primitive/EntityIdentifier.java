package jig.erd.domain.primitive;

public class EntityIdentifier {
    String schemaName;
    String entityName;

    public EntityIdentifier(String schemaName, String entityName) {
        this.schemaName = schemaName;
        this.entityName = entityName;
    }

    @Override
    public String toString() {
        return "EntityIdentifier{" +
                "schemaName='" + schemaName + '\'' +
                ", entityName='" + entityName + '\'' +
                '}';
    }
}
