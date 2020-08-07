package jig.erd.domain.primitive;

import java.util.List;

import static java.util.stream.Collectors.*;

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
                .collect(joining("|", "|", ""));
    }

    public String htmlColumnsText() {
        return list.stream()
                .map(column -> column.htmlNodeLabelText())
                .collect(joining());
    }
}
