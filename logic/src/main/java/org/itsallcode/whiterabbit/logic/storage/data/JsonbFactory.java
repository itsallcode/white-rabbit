package org.itsallcode.whiterabbit.logic.storage.data;

import java.util.Map;

import org.eclipse.yasson.YassonConfig;
import org.itsallcode.whiterabbit.api.model.ActivityData;
import org.itsallcode.whiterabbit.api.model.DayData;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

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
