package org.itsallcode.whiterabbit.plugin.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class DirectoryStreamProvider implements OutStreamProvider
{
    private static final Logger LOG = LogManager.getLogger(DirectoryStreamProvider.class);
    private final Path outPath;

    DirectoryStreamProvider(Path outPath)
    {
        this.outPath = outPath;
    }

    @Override
    public OutputStream getStream(String name) throws IOException
    {
        final String outFile = String.format("%s_working_time.csv", name);
        final Path path = outPath.resolve(outFile);
        LOG.info("Writing output to {}", path);
        return Files.newOutputStream(path);
    }
}
