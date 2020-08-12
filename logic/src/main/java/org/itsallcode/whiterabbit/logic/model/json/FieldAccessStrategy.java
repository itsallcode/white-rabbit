package org.itsallcode.whiterabbit.logic.model.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.config.PropertyVisibilityStrategy;

public class FieldAccessStrategy implements PropertyVisibilityStrategy
{
    @Override
    public boolean isVisible(Field field)
    {
        return true;
    }

    @Override
    public boolean isVisible(Method method)
    {
        return false;
    }
}
