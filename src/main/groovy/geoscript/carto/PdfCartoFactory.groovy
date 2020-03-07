package geoscript.carto

/**
 * A CartoFactory that creates a CartoBuilder that produces PDF documents.
 * @author Jared Erickson
 */
class PdfCartoFactory implements CartoFactory {

    @Override
    String getName() {
        "pdf"
    }

    @Override
    String getMimeType() {
        "application/pdf"
    }

    @Override
    CartoBuilder create(PageSize pageSize) {
        new PdfCartoBuilder(pageSize)
    }

}
