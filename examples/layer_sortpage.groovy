import geoscript.layer.*

def shp = new Shapefile("states.shp")

println "All States sorted by abbreviation descending:"
def c = shp.getCursor(sort: [["STATE_ABBR","DESC"]])
c.each{ state ->
    println "${state['STATE_ABBR']}"
}

println "First 10 States sorted by abbreviation ascending:"
c = shp.getCursor(sort: [["STATE_ABBR","ASC"]], start: 0, max: 10)
c.each{ state ->
    println "${state['STATE_ABBR']}"
}

println "Second 10 States sorted by abbreviation ascending:"
c = shp.getCursor(sort: [["STATE_ABBR","ASC"]], start: 10, max: 10)
c.each{ state ->
    println "${state['STATE_ABBR']}"
}
