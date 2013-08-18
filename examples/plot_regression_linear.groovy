import geoscript.layer.Shapefile
import geoscript.plot.Regression

def layer = new Shapefile("states.shp")
def data = layer.cursor.collect{f ->
    [
            f['PERSONS'],
            f['EMPLOYED']
    ]
}

Regression.linear(data, yLabel: "Employed", xLabel: "Persons", legend: false).show()
