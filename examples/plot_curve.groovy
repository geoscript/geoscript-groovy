import geoscript.layer.Shapefile
import geoscript.plot.Curve

def layer = new Shapefile("states.shp")
def data = layer.cursor.collect{f ->
    [
        f['PERSONS'],
        f['EMPLOYED']
    ]
}

Curve.curve(data, title: "# People vs. Employed", yLabel: "Employed", xLabel: "Persons", legend: false).show()
