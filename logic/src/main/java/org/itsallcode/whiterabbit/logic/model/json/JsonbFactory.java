package org.itsallcode.whiterabbit.logic.model.json;

import java.util.Map;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.eclipse.yasson.YassonConfig;
import org.itsallcode.whiterabbit.api.model.ActivityData;
import org.itsallcode.whiterabbit.api.model.DayData;

public class JsonbFactory
{
    public Jsonb create()
    {
        return createWithFormatting(true);
    }

    public Jsonb createNonFormatting()
    {
        return createWithFormatting(false);
    }

    private Jsonb createWithFormatting(boolean formatting)
    {
        final Map<Class<?>, Class<?>> userTypeMapping = Map.of(
                DayData.class, JsonDay.class,
                ActivityData.class, JsonActivity.class);
        final JsonbConfig config = new JsonbConfig()
                .withFormatting(formatting)
                .setProperty(YassonConfig.USER_TYPE_MAPPING, userTypeMapping);
        return JsonbBuilder.create(config);
    }
}
