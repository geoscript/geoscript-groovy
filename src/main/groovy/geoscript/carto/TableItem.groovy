package geoscript.carto

import java.awt.Font
import java.awt.Color

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

    TableItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    TableItem columns(List<String> names) {
        this.columns.addAll(names)
        this
    }

    TableItem column(String name) {
        this.columns.add(name)
        this
    }

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
