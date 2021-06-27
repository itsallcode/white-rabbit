package org.itsallcode.whiterabbit.plugin.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

class DirectoryStreamProvider implements OutStreamProvider
{

    private final Path outPath;

    DirectoryStreamProvider(Path outPath)
    {
        this.outPath = outPath;
    }

    @Override
    public OutputStream getStream(String name) throws IOException
    {
        final String outFile = String.format("%s_working_time.csv", name);
        return Files.newOutputStream(outPath.resolve(outFile));
    }
}
