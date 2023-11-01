package no.priv.bang.oldalbum.backend.imageio;

import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import no.priv.bang.oldalbum.services.ImageIOService;
import no.priv.bang.oldalbum.services.OldAlbumException;

@Component
public class ImageioSpiRegistration implements ImageIOService {

    @Reference(cardinality = ReferenceCardinality.MULTIPLE)
    public void registerImageReaderSpi(ImageReaderSpi readerProvider) {
        IIORegistry.getDefaultInstance().registerServiceProvider(readerProvider);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE)
    public void registerImageWriterSpi(ImageWriterSpi writerProvider) {
        IIORegistry.getDefaultInstance().registerServiceProvider(writerProvider);
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE)
    public void registerImageInputStreamSpi(ImageInputStreamSpi inputStreamProvider) {
        IIORegistry.getDefaultInstance().registerServiceProvider(inputStreamProvider);
    }

    @Override
    public ImageWriter getImageWriter(ImageReader reader) {
        try {
            var readerProvider = reader.getOriginatingProvider();
            var writerProviderClassNames = readerProvider.getImageWriterSpiNames();
            var witerProviderClass = Class.forName(writerProviderClassNames[0], true, reader.getClass().getClassLoader());
            var writerProvider = (ImageWriterSpi) IIORegistry.getDefaultInstance().getServiceProviderByClass(witerProviderClass);
            return writerProvider.createWriterInstance();
        } catch (Exception e) {
            throw new OldAlbumException(String.format("Failed to find image writer matching %s", reader.toString()), e);
        }
    }

}
