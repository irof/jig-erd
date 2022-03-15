package jig.erd.domain.primitive;

import java.util.regex.Pattern;

public class ColumnRelation {
    Column from;
    Column to;

    public ColumnRelation(Column from, Column to) {
        this.from = from;
        this.to = to;
    }

    public String edgeText() {
        return String.format("%s -> %s", from.edgeNodeText(), to.edgeNodeText());
    }

    EntityRelation toEntityRelation() {
        return new EntityRelation(from.entity(), to.entity());
    }

    public String readableLabel() {
        return String.format("%s -> %s", from.readableLabel(), to.readableLabel());
    }

    public boolean bothMatchSchema(Pattern schemaPattern) {
        return from.matchesSchema(schemaPattern) && to.matchesSchema(schemaPattern);
    }
}
