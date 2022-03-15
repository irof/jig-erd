package jig.erd.domain.diagram.overview;

import jig.erd.JigProperties;
import jig.erd.domain.primitive.Schema;
import jig.erd.domain.primitive.SchemaRelations;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class SchemaRelationDiagram {

    List<Schema> schemas;
    SchemaRelations schemaRelations;

    public SchemaRelationDiagram(List<Schema> schemas, SchemaRelations schemaRelations) {
        this.schemas = schemas;
        this.schemaRelations = schemaRelations;
    }

    public String dotText(JigProperties jigProperties) {
        String schemasText = schemas.stream()
                .map(schema -> schema.nodeText())
                .collect(Collectors.joining("\n"));

        String edgesText = schemaRelations.edgesText();

        return new StringJoiner("\n", "digraph ERD {\n", "}")
                .add("rankdir=" + jigProperties.rankdir() + ";")
                .add("node[shape=box,style=filled,fillcolor=lightyellow];")
                .add("edge[arrowhead=open, style=dashed];")
                .add(schemasText)
                .add(edgesText)
                .toString();
    }

    public SchemaRelationDiagram filter(JigProperties jigProperties) {
        return jigProperties.filterSchemaPattern()
                .map(schemaPattern -> new SchemaRelationDiagram(
                        schemas.stream().filter(schema -> schema.matchRegex(schemaPattern)).collect(Collectors.toList()),
                        schemaRelations.filter(jigProperties)
                ))
                .orElse(this);
    }
}
