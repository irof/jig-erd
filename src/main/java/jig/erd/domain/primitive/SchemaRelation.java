package jig.erd.domain.primitive;

public record SchemaRelation(Schema from, Schema to) {

    public String edgeText() {
        return String.format("%s -> %s", from.name(), to.name());
    }

    public boolean notSelf() {
        return !from.matches(to);
    }
}
