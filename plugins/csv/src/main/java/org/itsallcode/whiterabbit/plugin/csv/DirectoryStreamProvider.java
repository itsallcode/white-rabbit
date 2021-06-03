package org.itsallcode.whiterabbit.plugin.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DirectoryStreamProvider implements OutStreamProvider {

    private final String outPath;

    DirectoryStreamProvider(String outPath) {
        this.outPath= outPath;
    }

    @Override
    public OutputStream getStream(String name) throws IOException {
        final String outFile = String.format("%s_working_time.csv", name);
        return Files.newOutputStream(Paths.get(outPath, outFile));
    }
}
