package jig.erd.domain;

import jig.erd.JigProperties;
import jig.erd.domain.diagram.*;
import jig.erd.domain.primitive.*;

import java.util.List;
import java.util.Map;
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

    public Digraph schemaRelationDiagram() {
        return Digraph.schemaRelationDiagram(
                dotAttributes -> schemas.stream().map(schema -> schema.nodeText()).collect(Collectors.joining("\n")),
                columnRelations().toEntityRelations().toSchemaRelations()
        );
    }

    public Digraph entityRelationDiagram() {
        return Digraph.entityRelationDiagram(
                dotAttributes -> {
                    return schemas.stream()
                            .map(schema -> {
                                Entities allEntities = new Entities(entities);
                                SummarySchema summarySchema = new SummarySchema(schema, allEntities.only(schema));
                                return summarySchema.graphText(dotAttributes);
                            })
                            .collect(Collectors.joining("\n"));
                },
                columnRelations().toEntityRelations()
        );
    }

    public Digraph columnRelationDiagram() {
        return Digraph.columnRelationDiagram(
                dotAttributes -> {
                    Columns allColumns = new Columns(this.columns);
                    DetailEntities allEntities = entities.stream()
                            .map(entity -> new DetailEntity(entity, allColumns.only(entity)))
                            .collect(collectingAndThen(toList(), DetailEntities::new));
                    return schemas.stream()
                            .map(schema -> new DetailSchema(schema, allEntities.only(schema)))
                            .map(detailSchema -> detailSchema.graphText(dotAttributes)).collect(joining("\n"));
                },
                columnRelations()
        );
    }

    ColumnRelations columnRelations() {
        return new ColumnRelations(columnRelations);
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

    public Summary summary() {
        return new Summary(schemas.size(), entities.size(), columns.size(), columnRelations.size());
    }

    public Map<ViewPoint, String> mermaidTextMap() {
        // overview
        StringJoiner overviewText = new StringJoiner("\n", "graph LR\n", "");
        schemas.stream().map(Schema::name).forEach(overviewText::add);
        columnRelations().toEntityRelations().toSchemaRelations().stream()
                .map(edge -> edge.from().name() + " --> " + edge.to().name())
                .forEach(overviewText::add);

        // summary
        StringJoiner summaryText = new StringJoiner("\n", "graph LR\n", "");
        var entitySchemaMap = entities.stream().collect(groupingBy(Entity::schema));
        entitySchemaMap.entrySet().stream().map(entry -> {
            // schemaごとにまとめる
            // subgraphのIDは日本語を使用できない。重複してもいけないのでsg+indexとしておく。
            var schema = entry.getKey();
            StringJoiner text = new StringJoiner("\n", "subgraph sg%d[\"%s\"]\n".formatted(schemas.indexOf(schema), schema.name()), "\nend");
            entry.getValue().stream().map(Entity::name).forEach(text::add);
            return text.toString();
        }).forEach(summaryText::add);
        columnRelations().toEntityRelations().stream()
                .map(edge -> edge.from().name() + " --> " + edge.to().name())
                .forEach(summaryText::add);

        // detail
        StringJoiner detailText = new StringJoiner("\n", "erDiagram LR\n", "");
        var columnEntitySchemaMap = columns.stream().collect(groupingBy(column -> column.entity().schema(), groupingBy(Column::entity, toList())));
        columnEntitySchemaMap.entrySet().stream().map(schemaEntry -> {
            // schemaごとにまとめる
            // mermaidのerDiagramはsubgraphを使えない
            // StringJoiner schemaText = new StringJoiner("\n", "subgraph " + schemaEntry.getKey().name() + "\n", "\nend");
            StringJoiner schemaText = new StringJoiner("\n");
            schemaEntry.getValue().entrySet().stream().map(entityEntry -> {
                // entityの中身
                var entity = entityEntry.getKey();
                StringJoiner entityText = new StringJoiner("\n", "%s[\"%s\"]".formatted(entity.name(), entity.label()) + "{\n", "\n}");
                entityEntry.getValue().stream()
                        // 型、カラム名、制約、コメント を表示できる。型も制約も記録してないのでとりあえずカラム名だけ。
                        .map(column -> "%s %s".formatted("x", column.name()))
                        .forEach(entityText::add);
                return entityText.toString();
            }).forEach(schemaText::add);
            return schemaText.toString();
        }).forEach(detailText::add);
        columnRelations().toEntityRelations().stream()
                .map(edge -> edge.from().name() + " ||--o{ " + edge.to().name())
                .forEach(detailText::add);

        return Map.of(
                ViewPoint.俯瞰, overviewText.toString(),
                ViewPoint.概要, summaryText.toString(),
                ViewPoint.詳細, detailText.toString()
        );
    }
}
