package jig.erd.domain.primitive;

import java.util.Map;

public class DotAttributes {

    private final Map<String, String> attributes;

    public DotAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String rootRankdir() {
        return String.format("rankdir=%s;", attributes.getOrDefault(Keys.ROOT_RANKDIR, "RL"));
    }

    public String edgeDefault() {
        return "edge[arrowhead=open, style=dashed];";
    }

    public String rootSchemaColor() {
        return attributes.getOrDefault(Keys.ROOT_SCHEMA_COLOR, "lightyellow");
    }

    public String rootEntityColor() {
        return attributes.getOrDefault(Keys.ROOT_ENTITY_COLOR, "lightgoldenrod");
    }

    public static class Keys {
        public static final String PREFIX = "jig.erd.dot.";
        public static final String ROOT_RANKDIR = PREFIX + "root.rankdir";
        public static final String ROOT_SCHEMA_COLOR = PREFIX + "root.schemaColor";
        public static final String ROOT_ENTITY_COLOR = PREFIX + "root.entityColor";
    }
}
