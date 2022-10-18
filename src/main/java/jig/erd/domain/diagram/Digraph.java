package jig.erd.domain.diagram;

import jig.erd.JigProperties;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.Function;

public class Digraph {

    private final Function<JigProperties, String>[] contents;

    @SafeVarargs
    public Digraph(Function<JigProperties, String>... contents) {
        this.contents = contents;
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
