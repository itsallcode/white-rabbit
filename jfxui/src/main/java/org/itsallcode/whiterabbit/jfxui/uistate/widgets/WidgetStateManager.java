package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

public interface WidgetStateManager<T, M>
{
    void restore(T widget, M model);

    void watch(T widget, M model);

    M createEmptyModel(String id);
}
