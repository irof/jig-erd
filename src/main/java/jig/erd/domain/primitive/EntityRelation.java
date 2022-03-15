package jig.erd.domain.primitive;

import java.util.regex.Pattern;

public class EntityRelation {
    Entity from;
    Entity to;

    public EntityRelation(Entity from, Entity to) {
        this.from = from;
        this.to = to;
    }

    public String edgeText() {
        return String.format("%s -> %s", from.nodeIdText(), to.nodeIdText());
    }

    public SchemaRelation toSchemaRelation() {
        return new SchemaRelation(from.schema, to.schema);
    }

    public boolean bothMatches(Pattern schemaPattern) {
        return from.matchesSchema(schemaPattern) && to.matchesSchema(schemaPattern);
    }
}
