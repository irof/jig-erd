package jig.erd.domain.diagram;

import jig.erd.domain.primitive.DotAttributes;
import jig.erd.domain.primitive.Edges;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.Function;

public class Digraph {

    private final Function<DotAttributes, String>[] contents;

    @SafeVarargs
    public Digraph(Function<DotAttributes, String>... contents) {
        this.contents = contents;
    }

    public static Digraph schemaRelationDiagram(Function<DotAttributes, String> nodes, Edges edges) {
        return new Digraph(
                dotAttributes -> String.format("node[shape=box,style=filled,fillcolor=%s];", dotAttributes.rootSchemaColor()),
                nodes,
                edges::edgesText
        );
    }

    public static Digraph entityRelationDiagram(Function<DotAttributes, String> nodes, Edges edges) {
        return new Digraph(
                dotAttributes -> String.format("graph[style=filled,fillcolor=%s];", dotAttributes.rootSchemaColor()),
                dotAttributes -> String.format("node[shape=box,style=filled,fillcolor=%s];", dotAttributes.rootEntityColor()),
                nodes,
                edges::edgesText
        );
    }

    public static Digraph columnRelationDiagram(Function<DotAttributes, String> nodes, Edges edges) {
        return new Digraph(
                dotAttributes -> String.format("graph[style=filled,fillcolor=%s];", dotAttributes.rootSchemaColor()),
                // labelにtableで書き出すのでshapeしない
                dotAttributes -> "node[shape=plain];",
                nodes,
                edges::edgesText
        );
    }

    public String writeToString(DotAttributes dotAttributes) {
        StringJoiner digraphText = new StringJoiner("\n", "digraph ERD {\n", "}")
                .add(dotAttributes.rootRankdir())
                .add(dotAttributes.edgeDefault());

        Arrays.asList(contents).forEach(contentFunction -> {
            digraphText.add(contentFunction.apply(dotAttributes));
        });

        return digraphText.toString();
    }
}
