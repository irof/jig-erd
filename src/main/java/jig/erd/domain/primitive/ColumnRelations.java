package jig.erd.domain.primitive;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class ColumnRelations {
    List<ColumnRelation> list;

    public ColumnRelations(List<ColumnRelation> list) {
        this.list = list;
    }

    public String edgesText() {
        return list.stream()
                .map(columnRelation -> columnRelation.edgeText())
                .collect(Collectors.joining(";\n"));
    }

    public EntityRelations toEntityRelations() {
        return list.stream().map(ColumnRelation::toEntityRelation)
                .collect(collectingAndThen(toList(), EntityRelations::new));
    }
}
