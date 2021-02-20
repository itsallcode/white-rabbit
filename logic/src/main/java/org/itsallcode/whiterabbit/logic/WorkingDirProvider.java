package org.itsallcode.whiterabbit.logic;

import java.nio.file.Path;

public interface WorkingDirProvider
{
    Path getWorkingDir();

    Path getUserDir();
}
