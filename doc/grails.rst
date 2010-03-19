.. _grails:

Using GeoScript with Grails
===========================

Install Grails
--------------


Create a Web Application
------------------------

grails create-app geoscript

Copy Libraries
--------------

Copy all jar files from the GeoScript Groovy installation except xml-apis-1.0.b2.jar and xml-apis-xerces-2.7.1.jar to the lib
directory of your Grails application.

Create a Controller
-------------------

grails create-controller geoscript.Buffer

Edit Controller Code
--------------------

grails-app/controllers/geoscript/BufferController.groovy

package geoscript

import geoscript.geom.Geometry

class BufferController {

    def index = {
        def g = Geometry.fromWKT(params.geom)
        def d = params.distance as Double
        def wkt = g.buffer(d).wkt
        render(wkt)
    }

}

Try it out
----------

Run your Grails application:

grails run-app

And enter the following url:

http://localhost:8080/geoscript/buffer?geom=POINT%20(10%2010)&distance=7