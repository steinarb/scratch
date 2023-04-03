package no.priv.bang.modeling.modelstore.mocks;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A handwritten mock {@link OutputStream}.
 *
 * This is an {@link OutputStream} that will throw {@link IOException} on
 * all methods declaring {@link IOException}.
 *
 */
public class MockOutputStreamThatThrowsIOExceptionOnEverything extends OutputStream {

    @Override
    public void write(int b) throws IOException {
        throw new IOException();
    }

    @Override
    public void close() throws IOException {
        throw new IOException();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        throw new IOException();
    }

    @Override
    public void write(byte[] b) throws IOException {
        throw new IOException();
    }

}
