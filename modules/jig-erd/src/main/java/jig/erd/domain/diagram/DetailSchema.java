package jig.erd.domain.diagram;

import jig.erd.domain.graphviz.DotAttributes;
import jig.erd.domain.primitive.Schema;

import java.util.StringJoiner;

public class DetailSchema {
    Schema schema;
    DetailEntities entities;

    public DetailSchema(Schema schema, DetailEntities entities) {
        this.schema = schema;
        this.entities = entities;
    }

    public String graphText(DotAttributes dotAttributes) {
        return new StringJoiner("", "subgraph \"cluster_" + schema.name() + "\" {\n", "}\n")
                .add(String.format("label=\"%s\";\n", schema.name()))
                .add(entities.nodesText(dotAttributes))
                .toString();
    }
}
