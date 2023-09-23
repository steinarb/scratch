package no.priv.bang.oldalbum.backend.imageio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageWriterSpi;
import com.twelvemonkeys.imageio.stream.BufferedFileImageInputStreamSpi;

class ImageioSpiRegistrationTest {

    private static JPEGImageReaderSpi jpegReaderSpi;
    private static JPEGImageWriterSpi jpegWriterSpi;
    private static BufferedFileImageInputStreamSpi inputStreamSpi;

    @BeforeAll
    static void removeImageIOPlugins() {
        jpegReaderSpi = IIORegistry.getDefaultInstance().getServiceProviderByClass(JPEGImageReaderSpi.class);
        IIORegistry.getDefaultInstance().deregisterServiceProvider(jpegReaderSpi);
        jpegWriterSpi = IIORegistry.getDefaultInstance().getServiceProviderByClass(JPEGImageWriterSpi.class);
        IIORegistry.getDefaultInstance().deregisterServiceProvider(jpegWriterSpi);
        inputStreamSpi = IIORegistry.getDefaultInstance().getServiceProviderByClass(BufferedFileImageInputStreamSpi.class);
        IIORegistry.getDefaultInstance().deregisterServiceProvider(inputStreamSpi);
    }

    @Test
    void testImageReaderRegistration() {
        var readersBeforeAdd = getListFromIterator(ImageIO.getImageReadersByFormatName("JPEG"));
        assertThat(readersBeforeAdd).isNotEmpty();

        var component = new ImageioSpiRegistration();
        component.registerImageReaderSpi(jpegReaderSpi);

        var readersAfterAdd = getListFromIterator(ImageIO.getImageReadersByFormatName("JPEG"));
        assertThat(readersAfterAdd).hasSizeGreaterThan(readersBeforeAdd.size());
    }

    @Test
    void testImageWriterRegistration() {
        var writersBeforeAdd = getListFromIterator(ImageIO.getImageWritersByFormatName("JPEG"));
        assertThat(writersBeforeAdd).isNotEmpty();

        var component = new ImageioSpiRegistration();
        component.registerImageWriterSpi(jpegWriterSpi);

        var readersAfterAdd = getListFromIterator(ImageIO.getImageWritersByFormatName("JPEG"));
        assertThat(readersAfterAdd).hasSizeGreaterThan(writersBeforeAdd.size());
    }

    @Test
    void testImageInputStreamRegistration() {
        var instanceBeforeAdd = IIORegistry.getDefaultInstance().getServiceProviderByClass(BufferedFileImageInputStreamSpi.class);
        assertNull(instanceBeforeAdd);

        var component = new ImageioSpiRegistration();
        component.registerImageInputStreamSpi(inputStreamSpi);

        var instanceAfterAdd = IIORegistry.getDefaultInstance().getServiceProviderByClass(BufferedFileImageInputStreamSpi.class);
        assertEquals(inputStreamSpi, instanceAfterAdd);
    }

    public static <T> List<T> getListFromIterator(Iterator<T> iterator) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport
            .stream(iterable.spliterator(), false)
            .collect(Collectors.toList());
    }
}
