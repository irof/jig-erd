package jig.erd.domain.diagram.editor;

import jig.erd.JigProperties;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

public class Digraph {
    List<Function<JigProperties, String>> contents;

    @SafeVarargs
    public Digraph(Function<JigProperties, String>... contents) {
        this.contents = List.of(contents);
    }

    public String writeToString(JigProperties jigProperties) {
        StringJoiner digraph = new StringJoiner("\n", "digraph ERD {\n", "}")
                .add("rankdir=" + jigProperties.rankdir() + ";")
                .add("graph[style=filled,fillcolor=lightyellow];")
                //.add("node[shape=record,style=filled,fillcolor=lightgoldenrod];")
                .add("node[shape=plaintext];")
                .add("edge[arrowhead=open, style=dashed];");

        contents.forEach(contentFunction -> {
            digraph.add(contentFunction.apply(jigProperties));
        });

        return digraph.toString();
    }
}
