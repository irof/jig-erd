package jig.erd.domain.primitive;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class SchemaRelations {

    List<SchemaRelation> list;

    public SchemaRelations(List<SchemaRelation> list) {
        this.list = list;
    }

    public String edgesText() {
        return list.stream()
                .filter(SchemaRelation::notSelf)
                .map(SchemaRelation::edgeText)
                .sorted().distinct()
                .collect(joining(";\n", "", ";\n"));
    }
}
