package jig.erd.domain.primitive;

import jig.erd.JigProperties;

import java.util.List;

import static java.util.stream.Collectors.*;

public class EntityRelations {
    List<EntityRelation> list;

    EntityRelations(List<EntityRelation> list) {
        this.list = list;
    }

    public String edgesText() {
        return list.stream()
                .map(EntityRelation::edgeText)
                .sorted().distinct()
                .collect(joining(";\n", "", ";\n"));
    }

    public SchemaRelations toSchemaRelations() {
        return list.stream().map(EntityRelation::toSchemaRelation)
                .collect(collectingAndThen(toList(), SchemaRelations::new));
    }

    public EntityRelations filter(JigProperties jigProperties) {
        return jigProperties.filterSchemaPattern()
                .map(schemaPattern -> new EntityRelations(
                        list.stream().filter(entityRelation -> entityRelation.bothMatches(schemaPattern)).collect(toList())
                ))
                .orElse(this);
    }
}
