package jig.erd.domain.diagram.detail;

import jig.erd.domain.primitive.Schema;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class DetailEntities {
    List<DetailEntity> list;

    public DetailEntities(List<DetailEntity> list) {
        this.list = list;
    }

    public DetailEntities only(Schema schema) {
        return list.stream()
                .filter(detailEntity -> detailEntity.matches(schema))
                .collect(collectingAndThen(toList(), DetailEntities::new));
    }

    public String nodesText() {
        return list.stream()
                .map(DetailEntity::htmlNodeText)
                .collect(Collectors.joining(";\n"));
    }
}
