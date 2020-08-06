package jig.erd.domain.primitive;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class EntityRelations {
    List<EntityRelation> list;

    EntityRelations(List<EntityRelation> list) {
        this.list = list;
    }

    public String edgesText() {
        return list.stream()
                .map(EntityRelation::edgeText)
                .sorted().distinct()
                .collect(joining(";\n"));
    }
}
