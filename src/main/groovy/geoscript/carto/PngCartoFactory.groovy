package geoscript.carto

/**
 * A CartoFactory that creates a CartoBuilder that produces PNG images.
 * @author Jared Erickson
 */
class PngCartoFactory implements CartoFactory {

    @Override
    String getName() {
        "png"
    }

    @Override
    String getMimeType() {
        "image/png"
    }

    @Override
    CartoBuilder create(PageSize pageSize) {
        new ImageCartoBuilder(pageSize, ImageCartoBuilder.ImageType.PNG)
    }

}
