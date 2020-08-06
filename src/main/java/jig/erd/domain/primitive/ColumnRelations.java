package jig.erd.domain.primitive;

import java.util.List;
import java.util.stream.Collectors;

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
}
