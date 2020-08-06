package jig.erd.domain.primitive;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

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

    public Entities only(Schema schema) {
        return list.stream()
                .filter(entity -> entity.matches(schema))
                .collect(collectingAndThen(toList(), Entities::new));
    }
}
