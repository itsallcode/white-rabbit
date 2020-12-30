package org.itsallcode.whiterabbit.jfxui.uistate.widgets;

public interface WidgetState<T>
{
    void store(T widget);

    void restore(T widget);
}
