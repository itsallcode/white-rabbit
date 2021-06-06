package org.itsallcode.whiterabbit.plugin.csv;

import java.io.IOException;
import java.io.OutputStream;

public interface OutStreamProvider {
    OutputStream getStream(String name) throws IOException;
}
