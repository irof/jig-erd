package jig.erd.domain.diagram;

import java.util.Locale;

public enum DocumentFormat {
    SVG,
    PNG,
    DOT;

    public String extension() {
        return '.' + lowerCase();
    }

    private String lowerCase() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public String dotOption() {
        return "-T" + lowerCase();
    }
}
