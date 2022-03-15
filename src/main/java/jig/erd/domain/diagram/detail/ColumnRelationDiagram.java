package jig.erd.domain.diagram.detail;

import jig.erd.JigProperties;
import jig.erd.domain.primitive.ColumnRelations;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class ColumnRelationDiagram {

    List<DetailSchema> schemas;
    ColumnRelations columnRelations;

    public ColumnRelationDiagram(List<DetailSchema> schemas, ColumnRelations columnRelations) {
        this.schemas = schemas;
        this.columnRelations = columnRelations;
    }

    public String dotText(JigProperties jigProperties) {
        String schemasText = schemas.stream()
                .map(detailSchema -> detailSchema.graphText())
                .collect(joining("\n"));

        String edgesText = columnRelations.edgesText();

        return new StringJoiner("\n", "digraph ERD {\n", "}")
                .add("rankdir=" + jigProperties.rankdir() + ";")
                .add("graph[style=filled,fillcolor=lightyellow];")
                //.add("node[shape=record,style=filled,fillcolor=lightgoldenrod];")
                .add("node[shape=plaintext];")
                .add("edge[arrowhead=open, style=dashed];")
                .add(schemasText)
                .add(edgesText)
                .toString();
    }

    public ColumnRelationDiagram filter(JigProperties jigProperties) {
        return jigProperties.filterSchemaPattern()
                .map(pattern -> new ColumnRelationDiagram(
                        schemas.stream().filter(detailSchema -> detailSchema.matchesRegex(pattern)).collect(Collectors.toList()),
                        columnRelations.filter(jigProperties)
                ))
                .orElse(this);
    }
}
