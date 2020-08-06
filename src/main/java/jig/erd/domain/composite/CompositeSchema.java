package jig.erd.domain.composite;

import jig.erd.domain.primitive.Schema;

import java.util.StringJoiner;

public class CompositeSchema {
    Schema schema;
    CompositeEntities entities;

    public String graphText() {
        return new StringJoiner("", "subgraph cluster_" + schema.name() + " {\n", "}\n")
                .add(String.format("label=\"%s\";\n", schema.label()))
                .add(entities.nodesText())
                .toString();
    }
}
