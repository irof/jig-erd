package jig.erd.domain.diagram;

public enum ViewPoint {
    詳細("detail"),
    概要("summary"),
    俯瞰("");

    String suffix;

    ViewPoint(String suffix) {
        this.suffix = suffix;
    }

    public String suffix() {
        return suffix;
    }
}
