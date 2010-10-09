import geoscript.geom.*
import geoscript.viewer.Viewer

def geom = Geometry.fromWKT(request.getParameter("geom")).buffer(10)
def image = Viewer.drawToImage(geom, [400, 400])
response.contentType = "image/png"
javax.imageio.ImageIO.write(image, "png", response.outputStream)
