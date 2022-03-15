package jig.erd.domain.primitive;

import java.util.regex.Pattern;

public class SchemaRelation {
    Schema from;
    Schema to;

    public SchemaRelation(Schema from, Schema to) {
        this.from = from;
        this.to = to;
    }

    public String edgeText() {
        return String.format("%s -> %s", from.name(), to.name());
    }

    public boolean notSelf() {
        return !from.matches(to);
    }

    public boolean anyMatchSchema(Pattern schemaPattern) {
        return from.matchRegex(schemaPattern) && to.matchRegex(schemaPattern);
    }
}
