package jig.erd.domain.primitive;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class SchemaRelations implements Edges<Schema> {

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

    @Override
    public Stream<Edge<Schema>> stream() {
        return list.stream()
                .filter(SchemaRelation::notSelf)
                .distinct()
                .map(schemaRelation -> new Edge<>(schemaRelation.from(), schemaRelation.to()));
    }
}
