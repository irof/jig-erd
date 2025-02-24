package jig.erd.domain.primitive;

import java.util.List;

import static java.util.stream.Collectors.*;

public class EntityRelations implements Edges {
    List<EntityRelation> list;

    EntityRelations(List<EntityRelation> list) {
        this.list = list;
    }

    @Override
    public String edgesText(DotAttributes dotAttributes) {
        return list.stream()
                .map(EntityRelation::edgeText)
                .sorted().distinct()
                .collect(joining(";\n", "", ";\n"));
    }

    public SchemaRelations toSchemaRelations() {
        return list.stream().map(EntityRelation::toSchemaRelation)
                .collect(collectingAndThen(toList(), SchemaRelations::new));
    }
}
