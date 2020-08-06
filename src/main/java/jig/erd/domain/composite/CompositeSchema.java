package jig.erd.domain.composite;

import jig.erd.domain.primitive.Schema;

import java.util.StringJoiner;

public class CompositeSchema {
    Schema schema;
    CompositeEntities entities;

    public CompositeSchema(Schema schema, CompositeEntities entities) {
        this.schema = schema;
        this.entities = entities;
    }

    public String graphText() {
        return new StringJoiner("", "subgraph cluster_" + schema.name() + " {\n", "}\n")
                .add(String.format("label=\"%s\";\n", schema.name()))
                .add(entities.nodesText())
                .toString();
    }
}
