package jig.erd.domain.primitive;

import java.util.Objects;
import java.util.regex.Pattern;

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
        return String.format("\"%s.%s\"", schema.name(), name);
    }

    public String nodeText() {
        return String.format("%s[label=\"%s\" fillcolor=" + nodeColor() + "]", nodeIdText(), label());
    }

    private boolean highlight() {
        return label().startsWith("_");
    }

    public boolean matches(EntityIdentifier entityIdentifier) {
        return schema.name.equals(entityIdentifier.schemaName) && name.equals(entityIdentifier.entityName);
    }

    public boolean matches(Schema schema) {
        return this.schema.matches(schema);
    }

    public String readableLabel() {
        return schema.name() + '.' + name;
    }

    public String nodeColor() {
        if (highlight()) {
            return "orange";
        }
        return "lightgoldenrod";
    }

    public boolean matchesSchema(Pattern schemaPattern) {
        return schema.matchRegex(schemaPattern);
    }
}
