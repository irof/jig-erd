package jig.erd.domain.primitive;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class SchemaRelations implements Edges {

    List<SchemaRelation> list;

    public SchemaRelations(List<SchemaRelation> list) {
        this.list = list;
    }

    @Override
    public String edgesText(DotAttributes dotAttributes) {
        return list.stream()
                .filter(SchemaRelation::notSelf)
                .map(SchemaRelation::edgeText)
                .sorted().distinct()
                .collect(joining(";\n", "", ";\n"));
    }
}
