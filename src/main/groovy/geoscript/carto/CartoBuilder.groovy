package geoscript.carto

/**
 * Create a cartographic map
 * @author Jared Erickson
 */
interface CartoBuilder {

    /**
     * Add a date
     * @param dateTextItem The DateTextItem
     * @return The CartoBuilder
     */
    CartoBuilder dateText(DateTextItem dateTextItem)

    /**
     * Add an image
     * @param imageItem The ImageItem
     * @return The CartoBuilder
     */
    CartoBuilder image(ImageItem imageItem)

    /**
     * Add a line
     * @param lineItem The LineItem
     * @return The CartoBuilder
     */
    CartoBuilder line(LineItem lineItem)

    /**
     * Add a Map
     * @param mapItem The MapItem
     * @return The CartoBuilder
     */
    CartoBuilder map(MapItem mapItem)

    /**
     * Add an overview Map
     * @param overviewMapItem The OverviewMapItem
     * @return The CartoBuilder
     */
    CartoBuilder overViewMap(OverviewMapItem overviewMapItem)

    /**
     * Add a north arrow
     * @param northArrowItem The NorthArrowItem
     * @return The CartoBuilder
     */
    CartoBuilder northArrow(NorthArrowItem northArrowItem)

    /**
     * Add text
     * @param textItem The TextItem
     * @return The CartoBuilder
     */
    CartoBuilder text(TextItem textItem)

    /**
     * Add a paragraph
     * @param paragraphItem The ParagraphItem
     * @return The CartoBuilder
     */
    CartoBuilder paragraph(ParagraphItem paragraphItem)

    /**
     * Add a rectangle
     * @param rectangleItem The RectangleItem
     * @return The CartoBuilder
     */
    CartoBuilder rectangle(RectangleItem rectangleItem)

    /**
     * Add scale text
     * @param scaleTextItem The ScaleTextItem
     * @return The CartoBuilder
     */
    CartoBuilder scaleText(ScaleTextItem scaleTextItem)

    /**
     * Add scale bar
     * @param scaleBarItem The ScaleBarItem
     * @return The CartoBuilder
     */
    CartoBuilder scaleBar(ScaleBarItem scaleBarItem)

    /**
     * Add a grid (usually for visually placing other items)
     * @param gridItem The GridItem
     * @return The CartoBuilder
     */
    CartoBuilder grid(GridItem gridItem)

    /**
     * Add a table
     * @param tableItem The TableItem
     * @return The CartoBuilder
     */
    CartoBuilder table(TableItem tableItem)

    /**
     * Add a legend
     * @param legendItem The LegendItem
     * @return The CartoBuilder
     */
    CartoBuilder legend(LegendItem legendItem)

    /**
     * Write the cartographic document to the OutputStream
     * @param outputStream The OutputStream
     */
    void build(OutputStream outputStream)

}

