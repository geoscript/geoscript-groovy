package geoscript.carto

interface CartoFactory {

    CartoBuilder create(PageSize pageSize)

    String getName()

    String getMimeType()

}
