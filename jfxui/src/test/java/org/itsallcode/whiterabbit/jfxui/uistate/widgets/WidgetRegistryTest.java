package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.itsallcode.whiterabbit.jfxui.uistate.widgets.TableState.ColumnState;
import org.itsallcode.whiterabbit.jfxui.uistate.widgets.TreeTableState.TreeColumnState;
import org.junit.jupiter.api.Test;

class WidgetRegistryTest
{
    private static final String REGISTRY_WITH_SPLIT_PANE_JSON = "{\"splitPanes\":{\"splitPane1\":{\"dividerPositions\":[0.2,0.3],\"id\":\"sp1\"}},\"stages\":{},\"tables\":{},\"treeTables\":{}}";
    private static final String FILLED_REGISTRY_WITH_COLUMNS_JSON = "{\"splitPanes\":{},\"stages\":{\"stage1\":{\"height\":0.0,\"width\":0.0,\"x\":0.0,\"y\":0.0}},\"tables\":{\"table1\":{\"columns\":[{\"columnId\":\"col1\",\"width\":0.0}],\"tableId\":\"table1\"}},\"treeTables\":{\"treeTable1\":{\"columns\":[{\"columnId\":\"treeCol1\",\"width\":0.0}],\"treeTableId\":\"treeTable1\"}}}";
    private static final String EMPTY_REGISTRY_JSON = "{\"splitPanes\":{},\"stages\":{},\"tables\":{},\"treeTables\":{}}";
    private static final String FILLED_REGISTRY_JSON = "{\"splitPanes\":{},\"stages\":{\"stage1\":{\"height\":0.0,\"width\":0.0,\"x\":0.0,\"y\":0.0}},\"tables\":{\"table1\":{\"tableId\":\"table1\"}},\"treeTables\":{\"treeTable1\":{\"treeTableId\":\"treeTable1\"}}}";

    @Test
    void serializeEmptyRegistry()
    {
        assertSerialized(new WidgetRegistry(), EMPTY_REGISTRY_JSON);
    }

    @Test
    void serializeFilledRegistry()
    {
        final WidgetRegistry registry = new WidgetRegistry();
        registry.stages.put("stage1", new StageState());
        registry.tables.put("table1", new TableState("table1"));
        registry.treeTables.put("treeTable1", new TreeTableState("treeTable1"));

        assertSerialized(registry, FILLED_REGISTRY_JSON);
    }

    @Test
    void serializeRegistryWithSplitPane()
    {
        final WidgetRegistry registry = new WidgetRegistry();
        final SplitPaneState pane1 = new SplitPaneState("sp1");
        pane1.dividerPositions = List.of(0.2, 0.3);
        registry.splitPanes.put("splitPane1", pane1);

        assertSerialized(registry, REGISTRY_WITH_SPLIT_PANE_JSON);
    }

    @Test
    void serializeFilledRegistryWithColumns()
    {
        final WidgetRegistry registry = new WidgetRegistry();
        registry.stages.put("stage1", new StageState());
        final TableState table1 = new TableState("table1");
        table1.columns = new ArrayList<>();
        table1.columns.add(new ColumnState("col1"));
        registry.tables.put("table1", table1);
        final TreeTableState treeTable1 = new TreeTableState("treeTable1");
        treeTable1.columns = new ArrayList<>();
        treeTable1.columns.add(new TreeColumnState("treeCol1"));
        registry.treeTables.put("treeTable1", treeTable1);

        assertSerialized(registry, FILLED_REGISTRY_WITH_COLUMNS_JSON);
    }

    @Test
    void deserializeEmptyRegistry()
    {
        final WidgetRegistry registry = deserialize(EMPTY_REGISTRY_JSON);
        assertThat(registry.stages).isEmpty();
        assertThat(registry.tables).isEmpty();
        assertThat(registry.treeTables).isEmpty();
    }

    @Test
    void deserializeFilledRegistry()
    {
        final WidgetRegistry registry = deserialize(FILLED_REGISTRY_JSON);
        assertThat(registry.stages).hasSize(1);
        assertThat(registry.stages.get("stage1")).isNotNull();
        assertThat(registry.tables).hasSize(1);
        assertThat(registry.tables.get("table1")).isNotNull();
        assertThat(registry.treeTables).hasSize(1);
        assertThat(registry.treeTables.get("treeTable1")).isNotNull();
    }

    @Test
    void deserializeFilledRegistryWithColumns()
    {
        final WidgetRegistry registry = deserialize(FILLED_REGISTRY_WITH_COLUMNS_JSON);
        assertThat(registry.tables.get("table1").columns).hasSize(1);
        assertThat(registry.tables.get("table1").columns.get(0).columnId).isEqualTo("col1");
        assertThat(registry.treeTables.get("treeTable1").columns).hasSize(1);
        assertThat(registry.treeTables.get("treeTable1").columns.get(0).columnId).isEqualTo("treeCol1");
    }

    @Test
    void deserializeFilledRegistryWithSplitPane()
    {
        final WidgetRegistry registry = deserialize(REGISTRY_WITH_SPLIT_PANE_JSON);
        assertThat(registry.splitPanes).hasSize(1);
        assertThat(registry.splitPanes.get("splitPane1").dividerPositions).containsExactly(0.2, 0.3);
    }

    private WidgetRegistry deserialize(String json)
    {
        final Jsonb jsonb = JsonbBuilder.create();
        return jsonb.fromJson(json, WidgetRegistry.class);
    }

    private void assertSerialized(WidgetRegistry registry, String expectedJson)
    {
        final Jsonb jsonb = JsonbBuilder.create();
        final String json = jsonb.toJson(registry);
        assertThat(json).isEqualTo(expectedJson);
    }
}
