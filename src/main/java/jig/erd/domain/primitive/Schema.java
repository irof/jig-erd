package jig.erd.domain.primitive;

import java.util.regex.Pattern;

public class Schema {
    String name;

    public Schema(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public boolean matches(Schema schema) {
        return this.name.equals(schema.name);
    }

    public String nodeText() {
        return String.format("%s[label=\"%s\"]", name(), name());
    }

    public boolean matchRegex(Pattern regex) {
        return regex.matcher(name).matches();
    }
}
