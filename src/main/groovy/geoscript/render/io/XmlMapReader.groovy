package geoscript.render.io

import geoscript.geom.Bounds
import geoscript.layer.Renderables
import geoscript.render.Map as GMap
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult

/**
 * Read a Map from an XML String.
 * <pre>
 * {@code
 * <map>
 *   <width>400</width>
 *   <height>400</height>
 *   <type>png</type>
 *   <proj>EPSG:4326</proj>
 *   <backgroundColor>blue</backgroundColor>
 *   <fixAspectRatio>true</fixAspectRatio>
 *   <layers>
 *       <layer>
 *           <layertype>layer</layertype>
 *           <file>states.shp</file>
 *       </layer>
 *   </layers>
 *   <bounds>
 *       <minX>-135.911779</minX>
 *       <minY>36.993573</minY>
 *       <maxX>-96.536779</maxX>
 *       <maxY>51.405899</maxY>
 *   </bounds>
 * </map>
 * }
 * </pre>
 * @author Jared Erickson
 */
class XmlMapReader implements MapReader {

    @Override
    GMap read(String str) {
        XmlSlurper xmlSlurper = new XmlSlurper()
        GPathResult xml = xmlSlurper.parseText(str)
        read(xml)
    }

    GMap read(GPathResult xml) {
        GMap map = new GMap(
                width: getInt(xml.width?.text()?.toString(), 600),
                height: getInt(xml.height?.text()?.toString(), 400),
                type: xml.type?.text() ?: "png",
                backgroundColor: xml.backgroundColor?.text(),
                fixAspectRatio: getBoolean(xml.fixAspectRatio?.text(), true),
                layers: Renderables.getRenderables(getLayerMaps(xml.layers))
        )
        if (xml.proj?.text()) {
            map.proj = xml.proj.text()
        }
        if (xml.bounds?.text()) {
            Map bounds = [
                    minX: xml.bounds.minX.text() as double,
                    minY: xml.bounds.minY.text() as double,
                    maxX: xml.bounds.maxX.text() as double,
                    maxY: xml.bounds.maxY.text() as double
            ]
            map.bounds = new Bounds(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY, bounds?.proj)
        }
        map
    }

    private int getInt(String s, int defaultValue) {
        if (s) {
            s.toInteger()
        } else {
            defaultValue
        }
    }

    private boolean getBoolean(String s, boolean defaultValue) {
        if (s) {
            Boolean.parseBoolean(s)
        } else {
            defaultValue
        }
    }

    private List<Map> getLayerMaps(def xml) {
        xml.children().collect { def layerDef ->
            Map layerMap = [:]
            layerDef.children().each { def layer ->
                layerMap[layer.name()] = layer.text()
            }
            layerMap
        }
    }

}
