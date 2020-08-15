package org.itsallcode.whiterabbit.logic;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultWorkingDirProvider implements WorkingDirProvider
{
    @Override
    public Path getWorkingDir()
    {
        return Paths.get(".").toAbsolutePath();
    }
}
