package jig.erd.domain.primitive;

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
}
