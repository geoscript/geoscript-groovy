package geoscript.carto

/**
 * A CartoFactory that creates a CartoBuilder that produces SVG documents.
 * @author Jared Erickson
 */
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
