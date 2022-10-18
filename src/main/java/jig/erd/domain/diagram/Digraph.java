package jig.erd.domain.diagram;

import jig.erd.JigProperties;
import jig.erd.domain.primitive.Edges;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.Function;

public class Digraph {

    private final Function<JigProperties, String>[] contents;

    @SafeVarargs
    public Digraph(Function<JigProperties, String>... contents) {
        this.contents = contents;
    }

    public static Digraph schemaRelationDiagram(Function<JigProperties, String> nodes, Edges edges) {
        return new Digraph(
                jigProperties -> "node[shape=box,style=filled,fillcolor=lightyellow];",
                nodes,
                edges::edgesText
        );
    }

    public static Digraph entityRelationDiagram(Function<JigProperties, String> nodes, Edges edges) {
        return new Digraph(
                jigProperties -> "graph[style=filled,fillcolor=lightyellow];",
                jigProperties -> "node[shape=box,style=filled,fillcolor=lightgoldenrod];",
                nodes,
                edges::edgesText
        );
    }

    public static Digraph columnRelationDiagram(Function<JigProperties, String> nodes, Edges edges) {
        return new Digraph(
                jigProperties -> "graph[style=filled,fillcolor=lightyellow];",
                // labelにtableで書き出すのでshapeしない
                jigProperties -> "node[shape=plain];",
                nodes,
                edges::edgesText
        );
    }

    public String writeToString(JigProperties jigProperties) {
        StringJoiner digraphText = new StringJoiner("\n", "digraph ERD {\n", "}")
                .add("rankdir=" + jigProperties.rankdir() + ";")
                .add("edge[arrowhead=open, style=dashed];");

        Arrays.asList(contents).forEach(contentFunction -> {
            digraphText.add(contentFunction.apply(jigProperties));
        });

        return digraphText.toString();
    }
}
