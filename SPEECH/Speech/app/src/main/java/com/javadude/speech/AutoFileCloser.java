package com.javadude.speech;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public abstract class AutoFileCloser {
    private List<Closeable> closeables = new ArrayList<>();
    protected <T extends Closeable> T watch(T closeable) {
        closeables.add(0, closeable);
        return closeable;
    }
    protected abstract void doWork() throws Throwable ;
    public AutoFileCloser() {
        Throwable pending = null;
        try {
            doWork();

        } catch (Throwable t) {
            pending = t;
        } finally {
            for(Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (Throwable t) {
                        if (pending == null)
                            pending = t;
                    }
                }
            }
            if (pending != null) {
                if (pending instanceof Error)
                    throw (Error) pending;
                if (pending instanceof RuntimeException)
                    throw (RuntimeException) pending;
                throw new RuntimeException(pending);
            }
        }
    }
}
