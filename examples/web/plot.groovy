
import geoscript.geom.*
import geoscript.viewer.Viewer

def geom = Geometry.fromWKT(request.getParameter("geom"))
def image = Viewer.plotToImage(geom, [400, 400])
response.contentType = "image/png"
javax.imageio.ImageIO.write(image, "png", response.outputStream)
