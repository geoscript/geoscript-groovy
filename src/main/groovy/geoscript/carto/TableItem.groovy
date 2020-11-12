package geoscript.carto

import java.awt.Font
import java.awt.Color

/**
 * Add table to a cartographic document
 * @author Jared Erickson
 */
class TableItem extends Item {

    List<String> columns = []

    List<Map> rows = []

    RowStyle columnRowStyle = new RowStyle(
        backGroundColor: Color.decode("#696969"),
        font: new Font("Arial",  Font.BOLD, 14),
        textColor: Color.WHITE,
        strokeColor: Color.BLACK
    )

    RowStyle evenRowStyle = new RowStyle(
        backGroundColor: Color.decode("#DCDCDC"),
        font: new Font("Arial", Font.PLAIN, 12),
        textColor: Color.BLACK,
        strokeColor: Color.BLACK
    )

    RowStyle oddRowStyle = new RowStyle(
        backGroundColor: Color.WHITE,
        font: new Font("Arial", Font.PLAIN, 12),
        textColor: Color.BLACK,
        strokeColor: Color.BLACK
    )

    /**
     * Create table from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    TableItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the columns of the table
     * @param names The column names
     * @return The TableItem
     */
    TableItem columns(List<String> names) {
        this.columns.addAll(names)
        this
    }

    /**
     * Add a column to the table
     * @param name The column name
     * @return The TableItem
     */
    TableItem column(String name) {
        this.columns.add(name)
        this
    }

    /**
     * Add a row to the table
     * @param values A Map of values
     * @return The TableItem
     */
    TableItem row(Map values) {
        rows.add(values)
        this
    }

    private class RowStyle {
        Color backGroundColor = Color.WHITE
        Color textColor = Color.BLACK
        Font font = new Font("Arial", Font.PLAIN, 12)
        Color strokeColor = Color.BLACK

        @Override
        String toString() {
            return "background-color = ${backGroundColor}, text-color = ${textColor}, font = ${font}, stroke-color = ${strokeColor}"
        }
    }

    @Override
    String toString() {
        "TableItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, columns = ${columns}, rows = ${rows}, " +
                "column-style = (${columnRowStyle}), even-row-style = (${evenRowStyle}), odd-row-style = (${oddRowStyle}))"
    }

}
