package jig.erd.domain.diagram.summary;

import jig.erd.domain.primitive.Entities;
import jig.erd.domain.primitive.Schema;

import java.util.StringJoiner;
import java.util.regex.Pattern;

public class SummarySchema {
    Schema schema;
    Entities entities;

    public SummarySchema(Schema schema, Entities entities) {
        this.schema = schema;
        this.entities = entities;
    }

    public String graphText() {
        return new StringJoiner("", "subgraph \"cluster_" + schema.name() + "\" {\n", "}\n")
                .add(String.format("label=\"%s\";\n", schema.name()))
                .add(entities.nodesText())
                .toString();
    }

    public boolean matchRegex(Pattern regex) {
        return schema.matchRegex(regex);
    }
}
