package geoscript.carto

class SvgCartoFactory implements CartoFactory {

    @Override
    String getName() {
        "svg"
    }

    @Override
    String getMimeType() {
        "application/svg"
    }

    @Override
    CartoBuilder create(PageSize pageSize) {
        new SvgCartoBuilder(pageSize)
    }

}
