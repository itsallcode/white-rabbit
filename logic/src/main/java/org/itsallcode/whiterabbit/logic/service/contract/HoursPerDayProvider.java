package org.itsallcode.whiterabbit.logic.service.contract;

import java.time.Duration;
import java.util.Optional;

public interface HoursPerDayProvider
{
    Optional<Duration> getHoursPerDay();
}
