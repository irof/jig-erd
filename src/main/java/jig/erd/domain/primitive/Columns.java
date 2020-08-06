package jig.erd.domain.primitive;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class Columns {
    List<Column> list;

    public Columns(List<Column> list) {
        this.list = list;
    }

    public Columns only(Entity target) {
        return list.stream()
                .filter(column -> column.matches(target))
                .collect(collectingAndThen(toList(), list -> new Columns(list)));
    }

    public String recordNodeLabelText() {
        return list.stream()
                .map(column -> column.recordNodeLabelText())
                .collect(Collectors.joining("|", "|", ""));
    }
}
