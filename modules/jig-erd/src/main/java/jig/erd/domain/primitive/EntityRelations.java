package jig.erd.domain.primitive;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class EntityRelations implements Edges<Entity> {
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

    @Override
    public Stream<Edge<Entity>> stream() {
        return list.stream()
                .map(entityRelation -> new Edge<>(entityRelation.from(), entityRelation.to()));
    }

    public SchemaRelations toSchemaRelations() {
        return list.stream().map(EntityRelation::toSchemaRelation)
                .collect(collectingAndThen(toList(), SchemaRelations::new));
    }
}
