package org.itsallcode.whiterabbit.jfxui.uistate.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UiStateModelTest
{
    Jsonb jsonb;
    UiStateModel model;

    @BeforeEach
    void setUp()
    {
        jsonb = JsonbBuilder.create();
        model = null;
    }

    @Test
    void serializeEmptyModel()
    {
        model = new UiStateModel();
        assertSerialized("{\"splitPanes\":{},\"stages\":{},\"tables\":{}}");
    }

    @Test
    void deserializeEmptyModel()
    {
        model = deserialize("{\"splitPanes\":{},\"stages\":{},\"tables\":{}}");
        assertThat(model).isNotNull();
        assertThat(model.splitPanes).isEmpty();
        assertThat(model.tables).isEmpty();
        assertThat(model.stages).isEmpty();
    }

    @Test
    void serializeStageState()
    {
        model = new UiStateModel();
        final StageStateModel stage1 = new StageStateModel("id");
        stage1.height = 1;
        stage1.width = 2;
        stage1.x = 3;
        stage1.y = 4;
        model.stages.put("s1", stage1);
        assertSerialized(
                "{\"splitPanes\":{},\"stages\":{\"s1\":{\"height\":1.0,\"id\":\"id\",\"width\":2.0,\"x\":3.0,\"y\":4.0}},\"tables\":{}}");
    }

    @Test
    void deserializeStage()
    {
        model = deserialize(
                "{\"splitPanes\":{},\"stages\":{\"s1\":{\"height\":1.0,\"id\":\"id\",\"width\":2.0,\"x\":3.0,\"y\":4.0}},\"tables\":{}}");
        assertThat(model.stages).hasSize(1);
        final StageStateModel stage = model.stages.get("s1");
        assertThat(stage.height).isEqualTo(1);
        assertThat(stage.width).isEqualTo(2);
        assertThat(stage.x).isEqualTo(3);
        assertThat(stage.y).isEqualTo(4);
        assertThat(stage.id).isEqualTo("id");
    }

    @Test
    void serializeTableState()
    {
        model = new UiStateModel();
        final TableStateModel table1 = new TableStateModel("id");
        final ColumnStateModel col1 = new ColumnStateModel();
        col1.id = "colId";
        col1.width = 42.1;
        table1.columns = List.of(col1);
        model.tables.put("t1", table1);
        assertSerialized(
                "{\"splitPanes\":{},\"stages\":{},\"tables\":{\"t1\":{\"columns\":[{\"id\":\"colId\",\"width\":42.1}],\"id\":\"id\"}}}");
    }

    @Test
    void deserializeTableState()
    {
        model = deserialize(
                "{\"splitPanes\":{},\"stages\":{},\"tables\":{\"t1\":{\"columns\":[{\"id\":\"colId\",\"width\":42.1}],\"id\":\"id\"}}}");
        assertThat(model.tables).hasSize(1);
        final TableStateModel table = model.tables.get("t1");
        assertThat(table.id).isEqualTo("id");
        assertThat(table.columns).hasSize(1);
        assertThat(table.columns.get(0).id).isEqualTo("colId");
        assertThat(table.columns.get(0).width).isEqualTo(42.1);
    }

    @Test
    void serializeSplitPaneState()
    {
        model = new UiStateModel();
        final SplitPaneStateModel pane1 = new SplitPaneStateModel("id");
        pane1.dividerPositions = List.of(0.42);
        model.splitPanes.put("s1", pane1);
        assertSerialized(
                "{\"splitPanes\":{\"s1\":{\"dividerPositions\":[0.42],\"id\":\"id\"}},\"stages\":{},\"tables\":{}}");
    }

    @Test
    void deserializeSplitPaneState()
    {
        model = deserialize(
                "{\"splitPanes\":{\"s1\":{\"dividerPositions\":[0.42],\"id\":\"id\"}},\"stages\":{},\"tables\":{}}");
        assertThat(model.splitPanes).hasSize(1);
        final SplitPaneStateModel pane = model.splitPanes.get("s1");
        assertThat(pane.id).isEqualTo("id");
        assertThat(pane.dividerPositions).containsExactly(0.42);
    }

    private UiStateModel deserialize(String json)
    {
        final Jsonb jsonb = JsonbBuilder.create();
        return jsonb.fromJson(json, UiStateModel.class);
    }

    private void assertSerialized(String expectedJson)
    {
        final Jsonb jsonb = JsonbBuilder.create();
        final String json = jsonb.toJson(model);
        assertThat(json).isEqualTo(expectedJson);
    }
}
