package org.itsallcode.whiterabbit.logic;

import java.nio.file.Path;
import java.util.Locale;

public interface Config
{

    public static Config read(Path configFile)
    {
        return ConfigFile.read(configFile);
    }

    Path getDataDir();

    Locale getLocale();
}
