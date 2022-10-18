package jig.erd.domain.primitive;

import java.util.Map;
import java.util.Optional;

public class DotAttributes {

    private final Map<String, String> map;

    public DotAttributes(Map<String, String> map) {
        this.map = map;
    }

    public Optional<String> rootRankdir() {
        return Optional.ofNullable(map.get("root.rankdir"))
                .map(value -> "rankdir=" + value + ";");
    }

    public String rootEdge() {
        return "edge[arrowhead=open, style=dashed];";
    }
}
