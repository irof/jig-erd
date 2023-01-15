package jig.erd.domain;

public record Summary(int schemas, int entities, int columns, int columnRelations) {

    public String text() {
        return "schemas: %d, entities: %d, columns: %d, columnRelations: %d"
                .formatted(schemas(), entities(), columns(), columnRelations());
    }
}
