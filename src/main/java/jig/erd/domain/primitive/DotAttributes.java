package jig.erd.domain.primitive;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DotAttributes {
    private static final Logger logger = Logger.getLogger(DotAttributes.class.getName());

    /**
     * entityに設定できる属性。
     * デフォルト値などは <a href="https://graphviz.org/doc/info/attrs.html">Graphvizのドキュメント</a> を参照してください。
     */
    final Set<String> allowEntryAttributes = Set.of(
            "shape",
            "fillcolor",
            "color", "penwidth",
            "fontcolor", "fontsize",
            "width", "height", "fixedsize",
            "margin"
    );


    private final Map<String, String> attributes;
    private final Map<String, Customizer> customizers;

    public DotAttributes(Map<String, String> attributes) {
        this.attributes = new HashMap<>(attributes);
        this.attributes.putAll(Map.of(
                Keys.CUSTOM_PREFIX + "_.label-pattern", "_.+",
                Keys.CUSTOM_PREFIX + "_.fillcolor", "orange"
        ));
        this.customizers = this.attributes.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(Keys.CUSTOM_PREFIX))
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().substring(Keys.CUSTOM_PREFIX.length(), entry.getKey().indexOf('.', Keys.CUSTOM_PREFIX.length())),
                        Collectors.reducing(new Customizer(Customizer.NONE, Map.of()), Customizer::from, Customizer::merge)
                ));
        logger.info("customizers: " + customizers);
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

    public String entityColor(Entity entity) {
        return customizers.values().stream()
                .filter(customizer -> customizer.condition.test(entity))
                .map(customizer -> customizer.attributes.getOrDefault("fillcolor", rootEntityColor()))
                .findAny()
                .orElseGet(this::rootEntityColor);
    }

    public String additionalAttributesOf(Entity entity) {
        return customizers.values().stream()
                .filter(customizer -> customizer.condition.test(entity))
                .flatMap(customizer -> customizer.attributes.entrySet().stream()
                        .filter(entry -> allowEntryAttributes.contains(entry.getKey()))
                        .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue())))
                .collect(Collectors.joining(";", ";", ""));
    }

    public static class Keys {
        public static final String PREFIX = "jig.erd.dot.";
        public static final String ROOT_RANKDIR = PREFIX + "root.rankdir";
        public static final String ROOT_SCHEMA_COLOR = PREFIX + "root.schemaColor";
        public static final String ROOT_ENTITY_COLOR = PREFIX + "root.entityColor";

        public static final String CUSTOM_PREFIX = PREFIX + "custom.";
    }

    static class Customizer {
        Predicate<Entity> condition;
        Map<String, String> attributes;

        static final Predicate<Entity> NONE = entity -> false;

        public Customizer(Predicate<Entity> condition, Map<String, String> attributes) {
            this.condition = condition;
            this.attributes = attributes;
        }

        static Customizer from(Map.Entry<String, String> entry) {
            String key = entry.getKey();
            String value = entry.getValue();

            String attributeKey = key.substring(key.indexOf('.', Keys.CUSTOM_PREFIX.length() + 1) + 1);
            switch (attributeKey) {
                case "name-pattern":
                    return new Customizer(entity -> entity.nameMatches(value), Map.of());
                case "alias-pattern":
                    return new Customizer(entity -> entity.aliasMatches(value), Map.of());
                case "label-pattern":
                    return new Customizer(entity -> entity.label().matches(value), Map.of());
            }
            return new Customizer(NONE, Map.of(attributeKey, value));
        }

        Customizer merge(Customizer other) {
            if (condition == NONE) {
                if (attributes.isEmpty()) {
                    return other;
                } else {
                    if (other.attributes.isEmpty()) {
                        return new Customizer(other.condition, attributes);
                    } else {
                        Map<String, String> map = new HashMap<>();
                        map.putAll(attributes);
                        map.putAll(other.attributes);
                        return new Customizer(other.condition, map);
                    }
                }
            } else {
                if (other.attributes.isEmpty()) {
                    return this;
                } else {
                    Map<String, String> map = new HashMap<>();
                    map.putAll(attributes);
                    map.putAll(other.attributes);
                    return new Customizer(condition, map);
                }
            }
        }

        @Override
        public String toString() {
            return "Customizer{" +
                    "condition=" + condition +
                    ", attributes=" + attributes +
                    '}';
        }
    }
}
