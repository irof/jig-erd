package jig.erd.domain.primitive;

import jig.erd.JigProperties;

import java.util.List;

import static java.util.stream.Collectors.*;

public class ColumnRelations {
    List<ColumnRelation> list;

    public ColumnRelations(List<ColumnRelation> list) {
        this.list = list;
    }

    public String edgesText() {
        return list.stream()
                .map(columnRelation -> columnRelation.edgeText())
                .collect(joining(";\n", "", ";\n"));
    }

    public EntityRelations toEntityRelations() {
        return list.stream().map(ColumnRelation::toEntityRelation)
                .collect(collectingAndThen(toList(), EntityRelations::new));
    }

    public ColumnRelations filter(JigProperties jigProperties) {
        return jigProperties.filterSchemaPattern()
                .map(schemaPattern -> new ColumnRelations(
                        list.stream().filter(columnRelation -> columnRelation.bothMatchSchema(schemaPattern)).collect(toList())
                ))
                .orElse(this);
    }
}
