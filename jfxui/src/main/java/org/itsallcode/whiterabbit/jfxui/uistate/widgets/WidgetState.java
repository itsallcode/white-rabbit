package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

public interface WidgetState<T>
{
    void restore(T widget);

    void watch(T widget);
}
