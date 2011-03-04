import geoscript.print.*
import geoscript.layer.*
import geoscript.proj.*
import geoscript.style.*
import geoscript.map.*
import java.awt.*

def shp = new Shapefile("states.shp")
shp.style = new Style(new PolygonSymbolizer(
    fillColor: "#E6E6E6",
    strokeColor: "#4C4C4C",
    strokeWidth: 0.5f
))
def map = new Map(fixAspectRatio: true)
map.bounds = shp.bounds
map.setProj(new Projection("EPSG:4326"))
map.addLayer(shp)

def t = new Template(400, 400, [
    new RectangleItem(x:10, y:10, width: 380, height: 380, strokeWidth: 4),
    new RectangleItem(x:20, y:20, width: 360, height: 88),
    new RectangleItem(x:20, y:115, width: 360, height: 265),
    new TextItem(text: "United States Map", x: 25, y: 25, font: new Font("Arial", Font.BOLD, 16), width: 350, height: 40, halign: 'center', valign: 'top'),
    new ParagraphItem(text: "The United States of America (also referred to as the United States, the U.S., the USA, or America) is a federal constitutional republic comprising fifty states and a federal district. The country is situated mostly in central North America, where its forty-eight contiguous states and Washington, D.C., the capital district, lie between the Pacific and Atlantic Oceans, bordered by Canada to the north and Mexico to the south. The state of Alaska is in the northwest of the continent, with Canada to the east and Russia to the west across the Bering Strait. The state of Hawaii is an archipelago in the mid-Pacific. The country also possesses several territories in the Caribbean and Pacific.",
        x:25, y:47, width:350, font: new Font("Arial", Font.PLAIN, 7), color: new Color(100,100,100)),
    
    new DateTextItem(x: 25, y: 375, font: new Font("Arial", Font.ITALIC, 12)),
    new ImageItem(x: 275, y: 323, path: new File('usaflag.png')),
    new LineItem(x:25, y:315, width: 350, strokeColor: Color.BLUE, strokeWidth:3),
    new MapItem(x:25, y:25, width:350, height: 340, map: map),
    new ScaleTextItem(x:25, y:350, map:map),
    new NorthArrowItem(x:160, y:325, width: 30, height:50)
])
t.render("usa.pdf", "application/pdf")
t.render("usa.jpeg", "image/jpeg")
t.render("usa.png", "image/png")
t.render("usa.gif", "image/gif")
t.render("usa.svg", "image/svg+xml")
t.render()
