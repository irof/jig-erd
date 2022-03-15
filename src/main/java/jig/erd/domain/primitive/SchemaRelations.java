package jig.erd.domain.primitive;

import jig.erd.JigProperties;

import java.util.List;
import java.util.stream.Collectors;

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

    public SchemaRelations filter(JigProperties jigProperties) {
        return jigProperties.filterSchemaPattern()
                .map(schemaPattern -> new SchemaRelations(
                        list.stream().filter(schemaRelation -> schemaRelation.anyMatchSchema(schemaPattern)).collect(Collectors.toList())
                ))
                .orElse(this);
    }
}
