package jig.erd.domain.primitive;

import java.util.Objects;

public class Entity {
    Schema schema;
    String name;
    String alias;

    public Entity(Schema schema, String name, String alias) {
        this.schema = schema;
        this.name = name;
        this.alias = Objects.requireNonNullElse(alias, "");
    }

    public boolean matches(Entity entity) {
        return matches(entity.schema) && name.equals(entity.name);
    }

    public String label() {
        if (alias.isEmpty()) return name;
        return alias;
    }

    public String nodeIdText() {
        return String.format("\"%s.%s\"", schema, name);
    }

    public boolean matches(EntityIdentifier entityIdentifier) {
        return schema.name.equals(entityIdentifier.schemaName) && name.equals(entityIdentifier.entityName);
    }

    public boolean matches(Schema schema) {
        return this.schema.matches(schema);
    }
}
