package jig.erd.domain.primitive;

public class Entity {
    Schema schema;
    String name;
    String alias;

    public boolean matches(Entity entity) {
        return schema.equals(entity.schema) && name.equals(entity.name);
    }

    public String label() {
        if (alias == null) return name;
        return alias;
    }

    public String nodeIdText() {
        return String.format("\"%s.%s\"", schema, name);
    }
}
