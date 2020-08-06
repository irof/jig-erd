package jig.erd.domain.primitive;

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
}
