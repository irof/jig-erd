package jig.erd.domain.primitive;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schema schema = (Schema) o;
        return Objects.equals(name, schema.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
