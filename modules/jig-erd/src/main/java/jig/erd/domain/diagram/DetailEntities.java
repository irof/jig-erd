package jig.erd.domain.diagram;

import jig.erd.domain.graphviz.DotAttributes;
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

    public String nodesText(DotAttributes dotAttributes) {
        return list.stream()
                .map(detailEntity -> detailEntity.htmlNodeText(dotAttributes))
                .collect(Collectors.joining(";\n", "", ";\n"));
    }
}
