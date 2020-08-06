package jig.erd.domain.diagram;

import jig.erd.domain.composite.CompositeSchema;
import jig.erd.domain.primitive.ColumnRelation;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ColumnRelationDiagram {

    List<CompositeSchema> schemas;
    List<ColumnRelation> columnRelations;

    public ColumnRelationDiagram(List<CompositeSchema> schemas, List<ColumnRelation> columnRelations) {
        this.schemas = schemas;
        this.columnRelations = columnRelations;
    }

    public String dotText() {
        String schemasText = schemas.stream()
                .map(compositeSchema -> compositeSchema.graphText())
                .collect(Collectors.joining("\n"));

        String edgesText = columnRelations.stream()
                .map(columnRelation -> columnRelation.edgeText())
                .collect(Collectors.joining(";\n"));

        return new StringJoiner("\n", "digraph ERD {", "}")
                .add("rankdir=LR;")
                .add("graph[style=filled,fillcolor=lightyellow];")
                .add("node[shape=record,style=filled,fillcolor=lightgoldenrod];")
                .add("edge[arrowhead=open, style=dashed];")
                .add(schemasText)
                .add(edgesText).toString();
    }
}
