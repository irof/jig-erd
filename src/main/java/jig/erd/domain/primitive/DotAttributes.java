package jig.erd.domain.primitive;

import java.util.Map;

public class DotAttributes {

    private final Map<String, String> map;

    public DotAttributes(Map<String, String> map) {
        this.map = map;
    }

    public String rootRankdir() {
        return String.format("rankdir=%s;", map.getOrDefault("root.rankdir", "RL"));
    }

    public String edgeDefault() {
        return "edge[arrowhead=open, style=dashed];";
    }

    public String defaultSchemaColor() {
        return "lightyellow";
    }

    public String defaultEntityColor() {
        return "lightgoldenrod";
    }
}
