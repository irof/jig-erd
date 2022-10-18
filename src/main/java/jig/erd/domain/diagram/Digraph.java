package jig.erd.domain.diagram;

import jig.erd.JigProperties;
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
                jigProperties -> "node[shape=box,style=filled,fillcolor=lightyellow];",
                nodes,
                edges::edgesText
        );
    }

    public static Digraph entityRelationDiagram(Function<DotAttributes, String> nodes, Edges edges) {
        return new Digraph(
                jigProperties -> "graph[style=filled,fillcolor=lightyellow];",
                jigProperties -> "node[shape=box,style=filled,fillcolor=lightgoldenrod];",
                nodes,
                edges::edgesText
        );
    }

    public static Digraph columnRelationDiagram(Function<DotAttributes, String> nodes, Edges edges) {
        return new Digraph(
                jigProperties -> "graph[style=filled,fillcolor=lightyellow];",
                // labelにtableで書き出すのでshapeしない
                jigProperties -> "node[shape=plain];",
                nodes,
                edges::edgesText
        );
    }

    public String writeToString(DotAttributes dotAttributes) {
        StringJoiner digraphText = new StringJoiner("\n", "digraph ERD {\n", "}")
                .add(dotAttributes.rootEdge());
        dotAttributes.rootRankdir().ifPresent(digraphText::add);

        Arrays.asList(contents).forEach(contentFunction -> {
            digraphText.add(contentFunction.apply(dotAttributes));
        });

        return digraphText.toString();
    }
}
