package geoscript.carto

class PageSize {

    /**
     * The letter landscape 8.5 x 11 page size
     */
    static LETTER_LANDSCAPE = new PageSize(792, 612)
    
    /**
     * The tabloid landscape 11 x 17 page size
     */
    static TABLOID_LANDSCAPE = new PageSize(1224, 792)

    /**
     * The C sized landscape page size
     */
    static C_LANDSCAPE = new PageSize(1584, 1224)

    /**
     * The D sized landscape page size
     */
    static D_LANDSCAPE = new PageSize(2448, 1584)

    /**
     * The E sized landscape page size
     */
    static E_LANDSCAPE = new PageSize(3168, 2448)

    /**
     * The letter portrait 11 x 8.5 page size
     */
    static LETTER_PORTRAIT = new PageSize(612, 792)

    /**
     * The tabloid portraait 17 x 11 page size
     */
    static TABLOID_PORTRAIT = new PageSize(792, 1224)

    /**
     * The C sized portrait page size
     */
    static C_PORTRAIT = new PageSize(1224, 1584)

    /**
     * The D sized portrait page size
     */
    static D_PORTRAIT = new PageSize(1584, 2448)

    /**
     * The E sized portrait page size
     */
    static E_PORTRAIT = new PageSize(2448, 3168)

    final int width;

    final int height;

    PageSize(int width, int height) {
        this.width = width
        this.height = height
    }

    @Override
    String toString() {
        "PageSize(${width}, ${height})"
    }

}