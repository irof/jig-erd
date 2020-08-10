package jig.erd.domain.primitive;

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
}
