package jig.erd.domain.composite;

import jig.erd.domain.primitive.Columns;
import jig.erd.domain.primitive.Entity;

public class CompositeEntity {
    Entity entity;
    Columns columns;

    public String recordNodeText() {
        String label = entity.label() + columns.recordNodeLabelText();
        return String.format("%s[label=\"%s\"]", entity.nodeIdText(), label);
    }
}
