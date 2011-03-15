package org.codehaus.mojo.deb.jdpkg.ar;

import java.io.Closeable;

public interface CloseableIterable<T> extends Iterable<T>, Closeable {
}
