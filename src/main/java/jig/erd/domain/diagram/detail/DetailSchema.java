package jig.erd.domain.diagram.detail;

import jig.erd.domain.primitive.Schema;

import java.util.StringJoiner;
import java.util.regex.Pattern;

public class DetailSchema {
    Schema schema;
    DetailEntities entities;

    public DetailSchema(Schema schema, DetailEntities entities) {
        this.schema = schema;
        this.entities = entities;
    }

    public String graphText() {
        return new StringJoiner("", "subgraph \"cluster_" + schema.name() + "\" {\n", "}\n")
                .add(String.format("label=\"%s\";\n", schema.name()))
                .add(entities.nodesText())
                .toString();
    }

    public boolean matchesRegex(Pattern regex) {
        return schema.matchRegex(regex);
    }
}
