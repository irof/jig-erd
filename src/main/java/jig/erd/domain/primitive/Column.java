package jig.erd.domain.primitive;

import java.util.List;

public record Column(String name, String alias, String refId,
                     Entity entity) {

    /**
     * aliasを使用しないファクトリメソッド。
     * alias=columnName
     * aliasは現在未対応 issue#5
     */
    public static Column generateWithoutAlias(ColumnIdentifier columnIdentifier, String refId, Entity entity) {
        return new Column(columnIdentifier.columnName(), columnIdentifier.columnName(), refId, entity);
    }

    String label() {
        return alias;
    }

    public boolean matches(Entity entity) {
        return this.entity.matches(entity);
    }

    public String recordNodeLabelText() {
        return String.format("<%s> %s", refId, label());
    }

    public String htmlNodeLabelText() {
        return String.format("<TR><TD PORT=\"%s\" ALIGN=\"LEFT\">%s</TD></TR>", refId, label());
    }

    String edgeNodeText() {
        // node:port
        // portは""で囲ってはいけない
        return String.format("%s:%s", entity.nodeIdText(), refId);
    }

    public boolean matches(ColumnIdentifier columnIdentifier) {
        return new ColumnIdentifier(entity(), name()).equals(columnIdentifier);
    }

    public String readableLabel() {
        return entity.readableLabel() + '.' + name;
    }

    public boolean matchesSchema(List<Schema> schemas) {
        return entity.matchesSchema(schemas);
    }
}
