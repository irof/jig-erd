package jig.erd.infrastructure.database;

import jig.erd.domain.ErdRoot;
import jig.erd.domain.Summary;
import jig.erd.domain.primitive.ColumnIdentifier;
import jig.erd.domain.primitive.Schema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepositoryTest {

    @Test
    void test() throws Exception {
        Repository sut = new Repository();

        Schema schema1 = sut.getSchema("schema1");

        sut.registerEntity(schema1, "entity1", "エンティティ1");
        sut.registerEntity(schema1, "entity2", "エンティティ2");
        sut.registerEntity(schema1, "entity3", "エンティティ3");

        sut.registerRelation(
                new ColumnIdentifier(schema1.name(), "entity1", "column1"),
                new ColumnIdentifier(schema1.name(), "entity2", "column1"));
        sut.registerRelation(
                new ColumnIdentifier(schema1.name(), "entity1", "column2"),
                new ColumnIdentifier(schema1.name(), "entity2", "column2"));
        sut.registerRelation(
                new ColumnIdentifier(schema1.name(), "entity1", "column1"),
                new ColumnIdentifier(schema1.name(), "entity3", "column1"));

        ErdRoot erdRoot = sut.buildErdRoot();
        Summary summary = erdRoot.summary();

        assertEquals(1, summary.schemas());
        assertEquals(3, summary.entities());
        assertEquals(5, summary.columns());
        assertEquals(3, summary.columnRelations());
    }
}