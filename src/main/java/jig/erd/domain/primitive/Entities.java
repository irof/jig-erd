package jig.erd.domain.primitive;

import java.util.List;
import java.util.stream.Collectors;

public class Entities {
    List<Entity> list;

    public Entities(List<Entity> list) {
        this.list = list;
    }

    public String nodesText() {
        return list.stream()
                .map(Entity::nodeText)
                .collect(Collectors.joining(";\n"));
    }
}
