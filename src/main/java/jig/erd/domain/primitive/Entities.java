package jig.erd.domain.primitive;

import java.util.List;

import static java.util.stream.Collectors.*;

public class Entities {
    List<Entity> list;

    public Entities(List<Entity> list) {
        this.list = list;
    }

    public String nodesText() {
        return list.stream()
                .map(Entity::nodeText)
                .collect(joining(";\n", "", ";\n"));
    }

    public Entities only(Schema schema) {
        return list.stream()
                .filter(entity -> entity.matches(schema))
                .collect(collectingAndThen(toList(), Entities::new));
    }
}
