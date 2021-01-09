package org.itsallcode.whiterabbit.jfxui.uistate.model;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.annotation.JsonbVisibility;

import org.itsallcode.whiterabbit.logic.model.json.FieldAccessStrategy;

@JsonbVisibility(FieldAccessStrategy.class)
public class UiStateModel
{
    public Map<String, StageStateModel> stages = new HashMap<>();
    public Map<String, TableStateModel> tables = new HashMap<>();
    public Map<String, SplitPaneStateModel> splitPanes = new HashMap<>();
}
