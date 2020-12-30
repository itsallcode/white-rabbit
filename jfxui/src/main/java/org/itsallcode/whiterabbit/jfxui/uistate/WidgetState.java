package org.itsallcode.whiterabbit.jfxui.uistate;

interface WidgetState<T>
{
    void store(T widget);

    void restore(T widget);
}
