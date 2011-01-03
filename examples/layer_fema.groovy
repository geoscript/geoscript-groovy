/**
 * Save the "Current Disaster Declarations" from a FEMA GeoRSS data feed to a
 * Shapefile
 * http://gis.fema.gov/DataFeeds.html
 */
import geoscript.geom.*
import geoscript.geom.io.*
import geoscript.feature.*
import geoscript.layer.Layer
import geoscript.workspace.Directory

// Create a Schema 
Schema s = new Schema('fema',[
        ['the_geom','Polygon','EPSG:4326'],
        ['title','String'],
        ['link','String']
])

// Create a Shapefile Layer in the current directory
Directory dir = new Directory('.')
Layer layer = dir.create(s)

// Read and Parse the USGS feed
def url = "http://gis.fema.gov/geoserver/wms?service=WMS&version=1.1.0&request=GetMap&layers=FEMA:Current%20Disaster%20Declarations&styles=&bbox=-123.709,28.94,-69.021,49.0&width=899&height=330&srs=EPSG:4326&format=application/rss+xml"
def rss = new XmlSlurper().parse(url).declareNamespace(georss: "http://www.georss.org/georss")

// Feature counter
int c = 0

// Use the GeoRSSReader to extract the Geometry
def geoRssReader = new GeoRSSReader()

// For each entry in the RSS feed create a Feature
def features = rss.channel.item.collect{item ->
    def title = item.title.text()
    def link = item.link.text().trim()
    def georss = item.'georss:polygon'.text()
    def polygon = geoRssReader.read("<georss:polygon>${georss}</georss:polygon>")
    Feature f = s.feature([
        'title':title,
        'link':link,
        'the_geom': polygon
    ])
    f
}
println("Found ${features.size()} features!")
layer.add(features)
