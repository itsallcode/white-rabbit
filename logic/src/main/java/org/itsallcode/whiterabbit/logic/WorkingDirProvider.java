package org.itsallcode.whiterabbit.logic;

import java.nio.file.Path;

@FunctionalInterface
public interface WorkingDirProvider
{
    Path getWorkingDir();
}
