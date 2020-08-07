package jig.erd.domain.diagram.detail;

import jig.erd.domain.primitive.ColumnRelations;

import java.util.List;
import java.util.StringJoiner;

import static java.util.stream.Collectors.joining;

public class ColumnRelationDiagram {

    List<DetailSchema> schemas;
    ColumnRelations columnRelations;

    public ColumnRelationDiagram(List<DetailSchema> schemas, ColumnRelations columnRelations) {
        this.schemas = schemas;
        this.columnRelations = columnRelations;
    }

    public String dotText() {
        String schemasText = schemas.stream()
                .map(detailSchema -> detailSchema.graphText())
                .collect(joining("\n"));

        String edgesText = columnRelations.edgesText();

        return new StringJoiner("\n", "digraph ERD {\n", "}")
                .add("rankdir=LR;")
                .add("graph[style=filled,fillcolor=lightyellow];")
                //.add("node[shape=record,style=filled,fillcolor=lightgoldenrod];")
                .add("node[shape=plaintext];")
                .add("edge[arrowhead=open, style=dashed];")
                .add(schemasText)
                .add(edgesText)
                .toString();
    }
}
