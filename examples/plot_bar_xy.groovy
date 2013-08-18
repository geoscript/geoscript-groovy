import geoscript.layer.Shapefile
import geoscript.plot.Bar

def layer = new Shapefile("states.shp")
def data = layer.cursor.collect{f ->
    [
        f['PERSONS'],
        f['EMPLOYED']
    ]
}

Bar.xy(data, title: "# People vs. Employed", yLabel: "Employed", xLabel: "Persons", legend: false).show()
