package geoscript.carto

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
