def url = "http://earthquake.usgs.gov/earthquakes/feed/csv/1.0/hour"
def reader = new geoscript.layer.io.CsvReader("Lon","Lat")
def layer = reader.read(new URL(url).text)
println "# Earthquakes = ${layer.count}"
layer.eachFeature {f ->
    println f
}
