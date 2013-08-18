import geoscript.layer.Shapefile
import geoscript.plot.Bar

def layer = new Shapefile("states.shp")
def data = [:] as TreeMap
layer.cursor.each{f ->
    def letter = f['STATE_NAME'][0]
    if (!data.containsKey(letter)) {
        data[letter] = 0
    }
    data[letter] = data[letter]  + 1
}

Bar.category(data).show()
