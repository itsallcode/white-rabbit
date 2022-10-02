package org.itsallcode.whiterabbit.plugin.csv;

import java.io.IOException;
import java.io.OutputStream;

interface OutStreamProvider
{
    OutputStream getStream(String name) throws IOException;
}
