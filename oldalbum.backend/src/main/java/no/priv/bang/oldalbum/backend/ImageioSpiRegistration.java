package no.priv.bang.oldalbum.backend;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageInputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component
public class ImageioSpiRegistration {

    @Reference(cardinality = ReferenceCardinality.MULTIPLE)
    public void registerImageReaderSpi(ImageReaderSpi readerProvider) {
        IIORegistry.getDefaultInstance().registerServiceProvider(readerProvider);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE)
    public void registerImageWriterSpi(ImageWriterSpi writerProvider) {
        IIORegistry.getDefaultInstance().registerServiceProvider(writerProvider);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE)
    public void registerImageInputStreamSpi(ImageInputStream inputStreamProvider) {
        IIORegistry.getDefaultInstance().registerServiceProvider(inputStreamProvider);
    }

}
