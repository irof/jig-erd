package jig.erd.domain.primitive;

public class ColumnRelation {
    Column from;
    Column to;

    public String edgeText() {
        return String.format("%s -> %s", from.edgeNodeText(), to.edgeNodeText());
    }
}
