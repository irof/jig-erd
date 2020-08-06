package jig.erd.domain.primitive;

public class Column {
    String name;
    String alias;
    String refId;

    Entity entity;

    String label() {
        if (alias == null) return name;
        return alias;
    }

    public boolean matches(Entity entity) {
        return this.entity.matches(entity);
    }

    public String recordNodeLabelText() {
        return String.format("<%s> %s", refId, label());
    }

    String edgeNodeText() {
        // node:port
        // portは""で囲ってはいけない
        return String.format("%s:%s", entity.nodeIdText(), refId);
    }
}
