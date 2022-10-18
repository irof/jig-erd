package jig.erd.domain;

import jig.erd.JigProperties;
import jig.erd.domain.diagram.detail.DetailEntities;
import jig.erd.domain.diagram.detail.DetailEntity;
import jig.erd.domain.diagram.detail.DetailSchema;
import jig.erd.domain.diagram.editor.DotEditor;
import jig.erd.domain.diagram.summary.SummarySchema;
import jig.erd.domain.primitive.*;

import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class ErdRoot {
    static final Logger logger = Logger.getLogger(ErdRoot.class.getName());

    List<Schema> schemas;
    List<Entity> entities;
    List<Column> columns;
    List<ColumnRelation> columnRelations;

    public ErdRoot(List<Schema> schemas, List<Entity> entities, List<Column> columns, List<ColumnRelation> columnRelations) {
        this.schemas = schemas;
        this.entities = entities;
        this.columns = columns;
        this.columnRelations = columnRelations;
    }

    public DotEditor columnRelationDiagram() {
        Columns allColumns = new Columns(this.columns);

        DetailEntities allEntities = entities.stream()
                .map(entity -> new DetailEntity(entity, allColumns.only(entity)))
                .collect(collectingAndThen(toList(), DetailEntities::new));

        List<DetailSchema> detailSchemas = schemas.stream()
                .map(schema -> new DetailSchema(schema, allEntities.only(schema)))
                .collect(toList());

        return jigProperties -> {
            String schemasText = detailSchemas.stream()
                    .map(detailSchema -> detailSchema.graphText())
                    .collect(joining("\n"));

            String edgesText = columnRelations().edgesText();

            return new StringJoiner("\n", "digraph ERD {\n", "}")
                    .add("rankdir=" + jigProperties.rankdir() + ";")
                    .add("graph[style=filled,fillcolor=lightyellow];")
                    //.add("node[shape=record,style=filled,fillcolor=lightgoldenrod];")
                    .add("node[shape=plaintext];")
                    .add("edge[arrowhead=open, style=dashed];")
                    .add(schemasText)
                    .add(edgesText)
                    .toString();
        };
    }

    public DotEditor schemaRelationDiagram() {
        return jigProperties -> {
            String schemasText = schemas.stream()
                    .map(schema -> schema.nodeText())
                    .collect(Collectors.joining("\n"));

            SchemaRelations schemaRelations = columnRelations().toEntityRelations().toSchemaRelations();
            String edgesText = schemaRelations.edgesText();

            return new StringJoiner("\n", "digraph ERD {\n", "}")
                    .add("rankdir=" + jigProperties.rankdir() + ";")
                    .add("node[shape=box,style=filled,fillcolor=lightyellow];")
                    .add("edge[arrowhead=open, style=dashed];")
                    .add(schemasText)
                    .add(edgesText)
                    .toString();
        };
    }

    public DotEditor entityRelationDiagram() {
        List<SummarySchema> summarySchemas = schemas.stream()
                .map(schema -> new SummarySchema(schema, entities(schema)))
                .collect(toList());

        EntityRelations entityRelations = columnRelations().toEntityRelations();

        return jigProperties -> {
            String schemasText = summarySchemas.stream()
                    .map(summarySchema -> summarySchema.graphText())
                    .collect(Collectors.joining("\n"));

            String edgesText = entityRelations.edgesText();

            return new StringJoiner("\n", "digraph ERD {\n", "}")
                    .add("rankdir=" + jigProperties.rankdir() + ";")
                    .add("graph[style=filled,fillcolor=lightyellow];")
                    .add("node[shape=box,style=filled,fillcolor=lightgoldenrod];")
                    .add("edge[arrowhead=open, style=dashed];")
                    .add(schemasText)
                    .add(edgesText)
                    .toString();
        };
    }

    ColumnRelations columnRelations() {
        return new ColumnRelations(columnRelations);
    }

    Entities entities(Schema schema) {
        Entities allEntities = new Entities(entities);
        return allEntities.only(schema);
    }

    public ErdRoot filter(JigProperties jigProperties) {
        return jigProperties.filterSchemaPattern().map(
                pattern -> {
                    logger.info("loaded schemas: " + schemas);
                    logger.info("filter schema pattern: " + pattern);
                    List<Schema> filteredSchemas = this.schemas.stream().filter(schema -> schema.matchRegex(pattern)).collect(toList());
                    if (filteredSchemas.isEmpty()) {
                        logger.warning("パターンに合致するスキーマが存在しませんでした。全スキーマで出力します。");
                        return null;
                    }
                    return new ErdRoot(
                            filteredSchemas,
                            entities.stream().filter(entity -> entity.matchesSchema(filteredSchemas)).collect(toList()),
                            columns.stream().filter(column -> column.matchesSchema(filteredSchemas)).collect(toList()),
                            columnRelations.stream().filter(columnRelation -> columnRelation.bothMatchSchema(filteredSchemas)).collect(toList())
                    );
                }
        ).orElse(this);
    }

    public String summaryText() {
        return String.format("schemas: %d, entities: %d, columns: %d, columnRelations: %d",
                schemas.size(), entities.size(), columns.size(), columnRelations.size());
    }
}
