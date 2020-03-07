package geoscript.carto

/**
 * A CartoFactory that creates a CartoBuilder that produces JPEG images.
 * @author Jared Erickson
 */
class JpegCartoFactory implements CartoFactory {

    @Override
    String getName() {
        "jpeg"
    }

    @Override
    String getMimeType() {
        "image/jpeg"
    }

    @Override
    CartoBuilder create(PageSize pageSize) {
        new ImageCartoBuilder(pageSize, ImageCartoBuilder.ImageType.JPEG)
    }

}
