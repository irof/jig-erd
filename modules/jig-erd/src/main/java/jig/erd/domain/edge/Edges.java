package jig.erd.domain.edge;


import jig.erd.domain.graphviz.DotAttributes;

import java.util.stream.Stream;

public interface Edges<NODE> {
    String edgesText(DotAttributes dotAttributes);

    Stream<Edge<NODE>> stream();
}
