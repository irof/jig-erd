package jig.erd.domain.composite;

import java.util.List;
import java.util.stream.Collectors;

public class CompositeEntities {
    List<CompositeEntity> list;

    public String nodesText() {
        return list.stream()
                .map(CompositeEntity::recordNodeText)
                .collect(Collectors.joining(";\n"));
    }
}
