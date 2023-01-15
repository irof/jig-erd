package jig.erd.domain.primitive;

public record EntityRelation(Entity from, Entity to) {

    public String edgeText() {
        return String.format("%s -> %s", from.nodeIdText(), to.nodeIdText());
    }

    public SchemaRelation toSchemaRelation() {
        return new SchemaRelation(from.schema(), to.schema());
    }
}
