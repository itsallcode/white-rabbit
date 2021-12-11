package org.itsallcode.whiterabbit.jfxui.uistate.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

class UiStateModelTest
{
    private static final String EMPTY_JSON = "{\"splitPanes\":{},\"stages\":{},\"tables\":{},\"titledPanes\":{}}";
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
        assertSerialized(EMPTY_JSON);
    }

    @Test
    void deserializeEmptyModel()
    {
        model = deserialize(EMPTY_JSON);
        assertThat(model).isNotNull();
        assertThat(model.splitPanes).isEmpty();
        assertThat(model.tables).isEmpty();
        assertThat(model.stages).isEmpty();
        assertThat(model.titledPanes).isEmpty();
    }

    @Test
    void serializeStageState()
    {
        model = new UiStateModel();
        final StageStateModel stage1 = new StageStateModel("id");
        stage1.height = 1D;
        stage1.width = 2D;
        stage1.x = 3D;
        stage1.y = 4D;
        model.stages.put("s1", stage1);
        assertSerialized(
                "{\"splitPanes\":{},\"stages\":{\"s1\":{\"height\":1.0,\"id\":\"id\",\"width\":2.0,\"x\":3.0,\"y\":4.0}},\"tables\":{},\"titledPanes\":{}}");
    }

    @Test
    void deserializeStage()
    {
        model = deserialize(
                "{\"stages\":{\"s1\":{\"height\":1.0,\"id\":\"id\",\"width\":2.0,\"x\":3.0,\"y\":4.0}}}");
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
                "{\"splitPanes\":{},\"stages\":{},\"tables\":{\"t1\":{\"columns\":[{\"id\":\"colId\",\"width\":42.1}],\"id\":\"id\"}},\"titledPanes\":{}}");
    }

    @Test
    void deserializeTableState()
    {
        model = deserialize(
                "{\"tables\":{\"t1\":{\"columns\":[{\"id\":\"colId\",\"width\":42.1}],\"id\":\"id\"}}}");
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
                "{\"splitPanes\":{\"s1\":{\"dividerPositions\":[0.42],\"id\":\"id\"}},\"stages\":{},\"tables\":{},\"titledPanes\":{}}");
    }

    @Test
    void deserializeSplitPaneState()
    {
        model = deserialize(
                "{\"titledPanes\":{\"s1\":{\"expanded\":true,\"id\":\"id\"}}}");
        assertThat(model.titledPanes).hasSize(1);
        final TitledPaneStateModel pane = model.titledPanes.get("s1");
        assertThat(pane.id).isEqualTo("id");
        assertThat(pane.expanded).isTrue();
    }

    @Test
    void serializeTitledPaneState()
    {
        model = new UiStateModel();
        final TitledPaneStateModel pane1 = new TitledPaneStateModel("id1");
        pane1.expanded = true;
        model.titledPanes.put("s1", pane1);
        assertSerialized(
                "{\"splitPanes\":{},\"stages\":{},\"tables\":{},\"titledPanes\":{\"s1\":{\"expanded\":true,\"id\":\"id1\"}}}");
    }

    @Test
    void deserializeTitledPaneState()
    {
        model = deserialize(
                "{\"splitPanes\":{\"s1\":{\"dividerPositions\":[0.42],\"id\":\"id\"}}}");
        assertThat(model.splitPanes).hasSize(1);
        final SplitPaneStateModel pane = model.splitPanes.get("s1");
        assertThat(pane.id).isEqualTo("id");
        assertThat(pane.dividerPositions).containsExactly(0.42);
    }

    private UiStateModel deserialize(String json)
    {
        return jsonb.fromJson(json, UiStateModel.class);
    }

    private void assertSerialized(String expectedJson)
    {
        final String json = jsonb.toJson(model);
        assertThat(json).isEqualTo(expectedJson);
    }
}
