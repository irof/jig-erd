package jig.erd.domain.primitive;

public class Schema {
    String name;
    String alias;

    public String label() {
        if (alias == null) return name;
        return alias;
    }

    public String name() {
        return name;
    }
}
