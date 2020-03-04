package geoscript.carto

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
