package jig.erd.domain.primitive;


import java.util.stream.Stream;

public interface Edges<NODE> {
    String edgesText(DotAttributes dotAttributes);

    Stream<Edge<NODE>> stream();
}
