import geoscript.geom.*
import geoscript.feature.*
import geoscript.layer.Layer
import geoscript.workspace.Directory

// Create a Schema
Schema s = new Schema('earthquakes',[
        ['the_geom','Point','EPSG:4326'],
        ['title','String'],
        ['date','java.util.Date'],
        ['elevation','Double']
])

// Create a Shapefile Layer in the current directory
Directory dir = new Directory('.')
Layer layer = dir.create(s)

// Read and Parse the USGS feed
def url = "http://earthquake.usgs.gov/earthquakes/catalogs/1day-M2.5.xml"
def rss = new XmlParser().parse(url)

// Feature counter
int c = 0

// Date format string
String dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"

// For each entry in the RSS feed create a Feature
rss.entry.each{e ->
    def title = e.title.text()
    def date = Date.parse(dateFormat, e.updated.text())
    def coordinate = e."georss:point".text().split(" ")
    double x = coordinate[1] as Double
    double y = coordinate[0] as Double
    def point = new Point(x,y)
    def elev = e."georss:elev".text() as Double
    Feature f = s.feature([
        'title':title,
        'date':date,
        'elevation': elev,
        'the_geom': point
    ],"earthquake_${c}")
    layer.add(f)
    c++
}