package no.priv.bang.oldalbum.services;

import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

public interface ImageIOService {
    ImageWriter getImageWriter(ImageReader reader);
}
