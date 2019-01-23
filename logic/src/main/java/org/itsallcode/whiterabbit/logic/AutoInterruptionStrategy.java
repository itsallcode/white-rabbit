package org.itsallcode.whiterabbit.logic;

import java.time.LocalTime;

@FunctionalInterface
public interface AutoInterruptionStrategy {
	boolean shouldCreateInterruption(LocalTime beginOfInterruption);
}
