package jig.erd.domain.diagram.detail;

import jig.erd.domain.primitive.Columns;
import jig.erd.domain.primitive.Entity;
import jig.erd.domain.primitive.Schema;

import java.util.StringJoiner;

public class DetailEntity {
    Entity entity;
    Columns columns;

    public DetailEntity(Entity entity, Columns columns) {
        this.entity = entity;
        this.columns = columns;
    }

    public String recordNodeText() {
        String label = entity.label() + columns.recordNodeLabelText();
        return String.format("%s[label=\"%s\"]", entity.nodeIdText(), label);
    }

    public String htmlNodeText() {
        String label = new StringJoiner("")
                .add("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" BGCOLOR=\"lemonchiffon2\">")
                .add("<TR><TD ALIGN=\"CENTER\" CELLPADDING=\"5\" BGCOLOR=\"lightgoldenrod\"><B>" + entity.label() + "</B></TD></TR>")
                .add(columns.htmlColumnsText())
                .add("</TABLE>")
                .toString();
        return String.format("%s[label=<%s>]", entity.nodeIdText(), label);
    }

    public boolean matches(Schema schema) {
        return entity.matches(schema);
    }
}
