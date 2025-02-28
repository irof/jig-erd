package jig.erd.domain.primitive;

import jig.erd.domain.edge.Edge;
import jig.erd.domain.edge.Edges;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class ColumnRelations implements Edges<Column> {
    List<ColumnRelation> list;

    public ColumnRelations(List<ColumnRelation> list) {
        this.list = list;
    }

    @Override
    public String edgesText(DotAttributes dotAttributes) {
        return list.stream()
                .map(columnRelation -> columnRelation.edgeText())
                .collect(joining(";\n", "", ";\n"));
    }

    @Override
    public Stream<Edge<Column>> stream() {
        return list.stream()
                .map(columnRelation -> new Edge<>(columnRelation.from(), columnRelation.to()));
    }

    public EntityRelations toEntityRelations() {
        return list.stream().map(ColumnRelation::toEntityRelation)
                .collect(collectingAndThen(toList(), EntityRelations::new));
    }
}
