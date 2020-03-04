package geoscript.carto

interface CartoBuilder {

    CartoBuilder dateText(DateTextItem dateTextItem)

    CartoBuilder image(ImageItem imageItem)

    CartoBuilder line(LineItem lineItem)

    CartoBuilder map(MapItem mapItem)

    CartoBuilder northArrow(NorthArrowItem northArrowItem)

    CartoBuilder text(TextItem textItem)

    CartoBuilder paragraph(ParagraphItem paragraphItem)

    CartoBuilder rectangle(RectangleItem rectangleItem)

    CartoBuilder scaleText(ScaleTextItem scaleTextItem)

    CartoBuilder grid(GridItem gridItem)

    void build(OutputStream outputStream)

}

