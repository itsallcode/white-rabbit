package org.itsallcode.whiterabbit.jfxui.uistate.model;

class StateModelUtil
{
    private StateModelUtil()
    {
        // Not instantiable
    }

    static Double assertValidDouble(Number n)
    {
        final double d = n.doubleValue();
        if (Double.isInfinite(d))
        {
            return null;
        }
        if (Double.isNaN(d))
        {
            return null;
        }
        return d;
    }
}
