package org.itsallcode.whiterabbit.jfxui.uistate.model;

import static org.assertj.core.api.Assertions.assertThat;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.itsallcode.whiterabbit.jfxui.uistate.model.StageStateModel;
import org.itsallcode.whiterabbit.jfxui.uistate.model.UiStateModel;
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
        final StageStateModel stage1 = new StageStateModel();
        stage1.height = 1;
        stage1.width = 2;
        stage1.x = 3;
        stage1.y = 4;
        stage1.id = "id";
        model.stages.put("s1", stage1);
        assertSerialized(
                "{\"splitPanes\":{},\"stages\":{\"s1\":{\"height\":1.0,\"id\":\"id\",\"width\":2.0,\"x\":3.0,\"y\":4.0}},\"tables\":{}}");
    }

    @Test
    void deserializeStage()
    {
        model = deserialize(
                "{\"splitPanes\":{},\"stages\":{\"s1\":{\"height\":1.0,\"id\":\"id\",\"width\":2.0,\"x\":3.0,\"y\":4.0}},\"tables\":{}}");
        assertThat(model.stages.get("s1")).isInstanceOf(StageStateModel.class);
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
