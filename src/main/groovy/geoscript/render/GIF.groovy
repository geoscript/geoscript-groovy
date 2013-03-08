package geoscript.render

import javax.imageio.metadata.IIOMetadata
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageTypeSpecifier
import javax.imageio.IIOImage
import javax.imageio.metadata.IIOMetadataNode
import org.geotools.coverage.grid.io.imageio.IIOMetadataDumper
import com.sun.media.imageioimpl.plugins.gif.GIFImageWriter
import com.sun.media.imageioimpl.plugins.gif.GIFImageWriterSpi

/**
 * Render a Map to a GIF image.
 * @author Jared Erickson
 */
class GIF extends Image{

    /**
     * Create a new GIF
     */
    GIF() {
        super("gif")
    }

    /**
     * Render a list of GIF images as an animated GIF to a File.
     * @param images The List of GIF images
     * @param file The File
     * @param delay The delay between images in milliseconds
     * @param loop Whether to loop continuously or not
     */
    public void renderAnimated(List<BufferedImage> images, File file, int delay = 300, boolean loop = false) {
        def out = new FileOutputStream(file)
        renderAnimatedToOutputStream(images, out, delay, loop)
    }

    /**
     * Render a list of GIF images as an animated GIF to a byte array.
     * @param images The List of GIF images
     * @param delay The delay between images in milliseconds
     * @param loop Whether to loop continuously or not
     * @return The byte array of the animated GIF
     */
    public byte[] renderAnimated(List<BufferedImage> images, int delay = 300, boolean loop = false) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        renderAnimatedToOutputStream(images, out, delay, loop)
        out.toByteArray()
    }

    /**
     * Render a list of GIF images as an animated GIF to an OutputStream.
     * @param images The List of GIF images
     * @param out The OutputStream
     * @param delay The delay between images in milliseconds
     * @param loop Whether to loop continuously or not
     */
    private void renderAnimatedToOutputStream(List<BufferedImage> images, OutputStream out, int delay = 300, boolean loop = false) {
        def ios = ImageIO.createImageOutputStream(out)
        def w = new GIFImageWriter(new GIFImageWriterSpi())
        w.output = ios
        w.prepareWriteSequence(null)

        def wp = w.defaultWriteParam
        wp.compressionMode = ImageWriteParam.MODE_EXPLICIT
        wp.compressionType = "LZW"
        wp.compressionQuality = 0.75

        images.each{image ->
            def md = w.getDefaultImageMetadata(new ImageTypeSpecifier(image),wp)
            def t = new IIOMetadataTree(md)
            t.set(["GraphicControlExtension"],[delayTime: delay/10])
            if (loop) {
                def n = t.set(["ApplicationExtensions","ApplicationExtension"],[
                        applicationID: "NETSCAPE", authenticationCode: "2.0"
                ])
                n.userObject = [0x1,0,0] as byte[]
            }
            t.commit()
            w.writeToSequence(new IIOImage(image, null, md), wp)
        }

        w.endWriteSequence()
        ios.flush()
        ios.close()
    }

    /**
     * A helper class to add set XML attributes
     * to the image's metadata
     */
    private static class IIOMetadataTree {

        private IIOMetadata md

        private IIOMetadataNode root

        IIOMetadataTree(IIOMetadata md) {
            this.md = md
            this.root = md.getAsTree(md.nativeMetadataFormatName)
        }

        IIOMetadataNode set(List path, java.util.Map attr) {
            def n = find(root, path)
            attr.each{k,v ->
                n.setAttribute(k, v as String)
            }
            return n
        }

        IIOMetadataNode find(IIOMetadataNode node, List path) {
            path.each{p ->
                node = child(node, p)
            }
            node
        }

        IIOMetadataNode child(IIOMetadataNode node, String name) {
            IIOMetadataNode n
            int len = node.length
            (0..<len).each { i ->
                def nn = node.item(i)
                if (nn.nodeName.equalsIgnoreCase(name)) {
                    n = nn
                    return
                }
            }
            if (!n) {
                n = new IIOMetadataNode(name)
                node.appendChild(n)
            }
            return n
        }

        void commit() {
            md.setFromTree(md.nativeMetadataFormatName, root)
        }

        String dump() {
            def dumper = new IIOMetadataDumper(root)
            dumper.metadata
        }
    }
}
