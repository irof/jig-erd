package jig.erd.domain.primitive;

import java.util.List;

import static java.util.stream.Collectors.*;

public class ColumnRelations implements Edges {
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

    public EntityRelations toEntityRelations() {
        return list.stream().map(ColumnRelation::toEntityRelation)
                .collect(collectingAndThen(toList(), EntityRelations::new));
    }
}
