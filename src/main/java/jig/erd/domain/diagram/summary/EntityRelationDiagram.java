package jig.erd.domain.diagram.summary;

import jig.erd.JigProperties;
import jig.erd.domain.primitive.EntityRelations;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class EntityRelationDiagram {

    List<SummarySchema> schemas;
    EntityRelations entityRelations;

    public EntityRelationDiagram(List<SummarySchema> schemas, EntityRelations entityRelations) {
        this.schemas = schemas;
        this.entityRelations = entityRelations;
    }

    public String dotText(JigProperties jigProperties) {
        String schemasText = schemas.stream()
                .map(summarySchema -> summarySchema.graphText())
                .collect(Collectors.joining("\n"));

        String edgesText = entityRelations.edgesText();

        return new StringJoiner("\n", "digraph ERD {\n", "}")
                .add("rankdir=" + jigProperties.rankdir() + ";")
                .add("graph[style=filled,fillcolor=lightyellow];")
                .add("node[shape=box,style=filled,fillcolor=lightgoldenrod];")
                .add("edge[arrowhead=open, style=dashed];")
                .add(schemasText)
                .add(edgesText)
                .toString();
    }
}
