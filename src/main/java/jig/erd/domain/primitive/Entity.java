package jig.erd.domain.primitive;

import java.util.List;
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
        return String.format("\"%s.%s\"", schema.name(), name);
    }

    public String nodeText(DotAttributes dotAttributes) {
        return String.format("%s[label=\"%s\" fillcolor=" + nodeColor(dotAttributes) + "]", nodeIdText(), label());
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

    public String nodeColor(DotAttributes dotAttributes) {
        return dotAttributes.entityColor(this);
    }

    public boolean matchesSchema(List<Schema> schemas) {
        return schemas.contains(schema);
    }
}
