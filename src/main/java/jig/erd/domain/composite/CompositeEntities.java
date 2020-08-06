package jig.erd.domain.composite;

import jig.erd.domain.primitive.Schema;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class CompositeEntities {
    List<CompositeEntity> list;

    public CompositeEntities(List<CompositeEntity> list) {
        this.list = list;
    }

    public CompositeEntities only(Schema schema) {
        return list.stream()
                .filter(compositeEntity -> compositeEntity.matches(schema))
                .collect(collectingAndThen(toList(), CompositeEntities::new));
    }

    public String nodesText() {
        return list.stream()
                .map(CompositeEntity::recordNodeText)
                .collect(Collectors.joining(";\n"));
    }
}
