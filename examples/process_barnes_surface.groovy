import geoscript.geom.*
import geoscript.workspace.*
import geoscript.layer.*
import geoscript.feature.*
import geoscript.style.*
import geoscript.filter.Function
import geoscript.process.*
import geoscript.render.Map

Bounds bounds = new Bounds(0,0,10,10,"EPSG:4326")

Workspace workspace = new Memory()
Layer layer = workspace.create(new Schema("grid",[
    new Field("geom","Point","EPSG:4326"),
    new Field("row","int"),
    new Field("col","int"),
    new Field("value","double")
]))

bounds.generateGrid(10,10,"point", {g,c,r ->
    double value = 0
    if (c in [1,2] && r in [1,2]) value = 1
    if (c in [3,4] && r in [1,2,3,4] || c in [1,2] && r in [3,4]) value = 30
    if (c in [5,6] && r in [1,2,3,4,5,6] || c in [1,2,3,4] && r in [5,6]) value = 60
    if (c in [7,8] && r in [1,2,3,4,5,6,7,8] || c in [1,2,3,4,5,6] && r in [7,8]) value = 70
    if (c in [9,10] && r in [1,2,3,4,5,6,7,8,9,10] || c in [1,2,3,4,5,6,7,8] && r in [9,10]) value = 100
    layer.add([geom: g,col:c, row:r, value: value])
})

layer.style = new Shape("navy", 6, "circle", 0.55, 0) + new Label(new Function("Concatenate(value)")).point([0.5,0.5],[0,5])

Process process = new Process("vec:BarnesSurface")
results = process.execute([
    data: layer.cursor,
    valueAttr: "value",
    scale: 300,
    convergence: 0.3,
    passes: 2,
    minObservations: 1,
    maxObservationDistance: 0,
    pixelsPerCell: 1,
    noDataValue: -999,
    outputWidth: 100,
    outputHeight: 100,
    outputBBOX: layer.bounds
])
Raster raster = results.result
raster.style = new ColorMap(raster, ["#f7da22","#ecbe1d","#e77124","#d54927","#cf3a27", "#a33936", "#7f182a", "#68101a"])

Map map = new Map([layers:[raster, layer]])
map.bounds = layer.bounds.expandBy(3)
map.renderToImage()

