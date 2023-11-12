package no.priv.bang.oldalbum.backend;

import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;

public record ImageAndWriter(IIOImage image, ImageWriter writer) {

}
