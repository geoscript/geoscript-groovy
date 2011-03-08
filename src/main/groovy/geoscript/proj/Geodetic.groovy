package geoscript.proj

import geoscript.geom.Point
import javax.measure.unit.SI
import org.geotools.referencing.GeodeticCalculator
import org.geotools.referencing.datum.DefaultEllipsoid

/**
 * The Geodetic class can be used to calculate azimuths, distances, and Points on an Ellipsoid.
 * It is inspired by the wonderful pyproj library.
 * @author Jared Erickson
 */
class Geodetic {
    
    // Ellipsoids from pyproj
    static final DefaultEllipsoid MERIT = DefaultEllipsoid.createFlattenedSphere("MERIT 1983", 6378137.0, 298.257, SI.METER)
    static final DefaultEllipsoid SGC84 = DefaultEllipsoid.createFlattenedSphere("Soviet Geodetic System 85", 6378136.0, 298.257, SI.METER)
    static final DefaultEllipsoid GRS80 = DefaultEllipsoid.createFlattenedSphere("GRS 1980(IUGG, 1980)", 6378137.0, 298.257222101, SI.METER)
    static final DefaultEllipsoid IAU76 = DefaultEllipsoid.createFlattenedSphere("IAU 1976", 6378140.0, 298.257, SI.METER)
    static final DefaultEllipsoid AIRY = DefaultEllipsoid.createEllipsoid("Airy 1830", 6377563.396, 6356256.910, SI.METER)
    static final DefaultEllipsoid APL4_9 = DefaultEllipsoid.createFlattenedSphere("Appl. Physics. 1965", 6378137.0, 298.25, SI.METER)        
    static final DefaultEllipsoid NWL9D = DefaultEllipsoid.createFlattenedSphere("Naval Weapons Lab., 1965", 6378145.0, 298.25, SI.METER)
    static final DefaultEllipsoid MOD_AIRY  = DefaultEllipsoid.createEllipsoid("Modified Airy", 6377340.189, 6356034.446, SI.METER)
    static final DefaultEllipsoid ANDRAE = DefaultEllipsoid.createFlattenedSphere("Andrae 1876 (Den., Iclnd.)", 6377104.43, 300.0, SI.METER)
    static final DefaultEllipsoid AUST_SA = DefaultEllipsoid.createFlattenedSphere("Australian Natl & S. Amer. 1969", 6378160.0, 298.25, SI.METER)
    static final DefaultEllipsoid GRS67 = DefaultEllipsoid.createFlattenedSphere("GRS 67(IUGG 1967)", 6378160.0, 298.2471674270, SI.METER)
    static final DefaultEllipsoid BESSEL = DefaultEllipsoid.createFlattenedSphere("Bessel 1841", 6377397.155, 299.1528128, SI.METER)
    static final DefaultEllipsoid BESS_NAM = DefaultEllipsoid.createFlattenedSphere("Bessel 1841 (Namibia)", 6377483.865, 299.1528128, SI.METER)
    static final DefaultEllipsoid CLRK66 = DefaultEllipsoid.createEllipsoid("Clarke 1866", 6378206.4, 6356583.8, SI.METER)
    static final DefaultEllipsoid CLRK80 = DefaultEllipsoid.createFlattenedSphere("Clarke 1880 mod.", 6378249.145, 293.4663, SI.METER)
    static final DefaultEllipsoid CPM = DefaultEllipsoid.createFlattenedSphere("Comm. des Poids et Mesures 1799", 6375738.7, 334.29, SI.METER)
    static final DefaultEllipsoid DELMBR = DefaultEllipsoid.createFlattenedSphere("Delambre 1810 (Belgium)", 6376428.0, 311.5, SI.METER)
    static final DefaultEllipsoid ENGELIS = DefaultEllipsoid.createFlattenedSphere("Engelis 1985", 6378136.05, 298.2566, SI.METER)
    static final DefaultEllipsoid EVRST30 = DefaultEllipsoid.createFlattenedSphere("Everest 1830", 6377276.345, 300.8017, SI.METER)
    static final DefaultEllipsoid EVRST48 = DefaultEllipsoid.createFlattenedSphere("Everest 1948", 6377304.063, 300.8017, SI.METER)
    static final DefaultEllipsoid EVRST56 = DefaultEllipsoid.createFlattenedSphere("Everest 1956", 6377301.243, 300.8017, SI.METER)
    static final DefaultEllipsoid EVRST69 = DefaultEllipsoid.createFlattenedSphere("Everest 1969", 6377295.664, 300.8017, SI.METER)
    static final DefaultEllipsoid EVRSTSS = DefaultEllipsoid.createFlattenedSphere("Everest (Sabah & Sarawak)", 6377298.556, 300.8017, SI.METER)
    static final DefaultEllipsoid FSCHR60 = DefaultEllipsoid.createFlattenedSphere("Fischer (Mercury Datum) 1960", 6378166.0, 298.3, SI.METER)
    static final DefaultEllipsoid FSCHR60M = DefaultEllipsoid.createFlattenedSphere("Modified Fischer 1960", 6378155.0, 298.3, SI.METER)
    static final DefaultEllipsoid FSCHR68 = DefaultEllipsoid.createFlattenedSphere("Fischer 1968", 6378150.0, 298.3, SI.METER)
    static final DefaultEllipsoid HELMERT = DefaultEllipsoid.createFlattenedSphere("Helmert 1906", 6378200.0, 298.3, SI.METER)
    static final DefaultEllipsoid HOUGH = DefaultEllipsoid.createFlattenedSphere("Hough", 6378270.0, 297.0, SI.METER)
    static final DefaultEllipsoid INTL = DefaultEllipsoid.createFlattenedSphere("International 1909 (Hayford)", 6378388.0,297.0, SI.METER)
    static final DefaultEllipsoid KRASS = DefaultEllipsoid.createFlattenedSphere("Krassovsky, 1942", 6378245.0, 298.3, SI.METER)
    static final DefaultEllipsoid KAULA = DefaultEllipsoid.createFlattenedSphere("Kaula 1961", 6378163.0, 298.24, SI.METER)
    static final DefaultEllipsoid LERCH = DefaultEllipsoid.createFlattenedSphere("Lerch 1979", 6378139.0, 298.257, SI.METER)
    static final DefaultEllipsoid MPRTS = DefaultEllipsoid.createFlattenedSphere("Maupertius 1738", 6397300.0, 191.0, SI.METER)
    static final DefaultEllipsoid NEW_INTL = DefaultEllipsoid.createEllipsoid("New International 1967", 6378157.5, 6356772.2, SI.METER)
    static final DefaultEllipsoid PLESSIS = DefaultEllipsoid.createEllipsoid("Plessis 1817 (France)", 6376523.0, 6355863.0, SI.METER)
    static final DefaultEllipsoid SEASIA = DefaultEllipsoid.createEllipsoid("Southeast Asia", 6378155.0,6356773.3205, SI.METER)
    static final DefaultEllipsoid WALBECK = DefaultEllipsoid.createEllipsoid("Walbeck", 6376896.0, 6355834.8467, SI.METER)
    static final DefaultEllipsoid WGS60 = DefaultEllipsoid.createFlattenedSphere("WGS 60", 6378165.0, 298.3, SI.METER)
    static final DefaultEllipsoid WGS66 = DefaultEllipsoid.createFlattenedSphere("WGS 66", 6378145.0, 298.25, SI.METER)
    static final DefaultEllipsoid WGS72 = DefaultEllipsoid.createFlattenedSphere("WGS 72", 6378135.0, 298.26, SI.METER)
    static final DefaultEllipsoid WGS84 = DefaultEllipsoid.createFlattenedSphere("WGS 84", 6378137.0, 298.257223563, SI.METER)
    static final DefaultEllipsoid SPHERE = DefaultEllipsoid.createEllipsoid("Normal Sphere (r=6370997)", 6370997.0, 6370997.0, SI.METER)
        
    // Put the Ellipsoids in a Map by name for easy lookup
    static final Map<String, DefaultEllipsoid> ellipsoids = new HashMap<String, DefaultEllipsoid>()
    static {
        ellipsoids.put("merit", MERIT)
        ellipsoids.put("sgc84", SGC84)
        ellipsoids.put("grs80", GRS80)
        ellipsoids.put("iau76", IAU76)
        ellipsoids.put("airy",  AIRY)
        ellipsoids.put("apl4_9",  APL4_9)
        ellipsoids.put("nwl9d",  NWL9D)
        ellipsoids.put("mod_airy",  MOD_AIRY)
        ellipsoids.put("andrae",  ANDRAE)
        ellipsoids.put("aust_sa",  AUST_SA)
        ellipsoids.put("grs67",  GRS67)
        ellipsoids.put("bessel",  BESSEL)
        ellipsoids.put("bess_nam",  BESS_NAM)
        ellipsoids.put("clrk66",  CLRK66)
        ellipsoids.put("clrk80",  CLRK80)
        ellipsoids.put("cpm",  CPM)
        ellipsoids.put("delmbr",  DELMBR)
        ellipsoids.put("engelis",  ENGELIS)
        ellipsoids.put("evrst30",  EVRST30)
        ellipsoids.put("evrst48",  EVRST48)
        ellipsoids.put("evrst56",  EVRST56)
        ellipsoids.put("evrst69",  EVRST69)
        ellipsoids.put("evrstss",  EVRSTSS)
        ellipsoids.put("fschr60",  FSCHR60)
        ellipsoids.put("fschr60m",  FSCHR60M)
        ellipsoids.put("fschr68",  FSCHR68)
        ellipsoids.put("helmert",  HELMERT)
        ellipsoids.put("hough",  HOUGH)
        ellipsoids.put("intl",  INTL)
        ellipsoids.put("krass",  KRASS)
        ellipsoids.put("kaula",  KAULA)
        ellipsoids.put("lerch",  LERCH)
        ellipsoids.put("mprts",  LERCH)
        ellipsoids.put("new_intl",  NEW_INTL)
        ellipsoids.put("plessis",  PLESSIS)
        ellipsoids.put("seasia",  SEASIA)
        ellipsoids.put("walbeck",  WALBECK)
        ellipsoids.put("wgs60",  WGS60)
        ellipsoids.put("wgs66",  WGS66)
        ellipsoids.put("wgs72",  WGS72)
        ellipsoids.put("wgs84",  WGS84)
        ellipsoids.put("sphere",  SPHERE)
    }
    
    /**
     * The GeoTools Ellipsoid used in calculations
     */
    final DefaultEllipsoid ellipsoid
    
    /**
     * Create a new Geodetic using the default WGS84 ellipsoid.
     */
    Geodetic() {
        this(WGS84)
    }

    /**
     * Create a new Geodetic using the name to look up an ellipsoid.
     * @param ellipsoid The name of the ellipsoid.
     */
    Geodetic(String ellipsoid) {
        this(ellipsoids.get(ellipsoid.toLowerCase()))
    }

    /**
     * Create a new Geodetic using the GeoTools DefaultEllipsoid 
     * @param ellipsoid The GeoTools DefaultEllipsoid
     */
    Geodetic(DefaultEllipsoid ellipsoid) {
        this.ellipsoid = ellipsoid
    }

    /**
     * Calculate a new Point and back azimuth given the starting Point, azimuth, and distance.
     * @param pt The starting Point
     * @param azimuth The azimuth
     * @param distance The distance
     * @return A Map with point and backAzimuth entries
     */
    def forward(Point pt,  double azimuth, double distance) {

        // Create GeodeticCalculator with the selected Ellipsoid
        def calc = new GeodeticCalculator(ellipsoid)

        // Set starting and ending geographic points
        calc.setStartingGeographicPoint(pt.x, pt.y)

        // Set the direction (azimuth and distance)
        calc.setDirection(azimuth, distance)

        // Get the destination point
        def destinationPt = calc.destinationGeographicPoint
           
        // Calculate the back azimuth
        calc.setStartingGeographicPoint(destinationPt.x, destinationPt.y)
        calc.setDestinationGeographicPoint(pt.x, pt.y)
        double backAzimuth = calc.azimuth
    
        // Return a Map of values
        [
            point: new Point(destinationPt.x, destinationPt.y),
            backAzimuth: backAzimuth
        ]
    }

    /**
     * Calculate the forward and back azimuth and distance between the given two Points.
     * @param pt1 The starting Point
     * @param pt2 The ending Point
     * @return A Map with forwardAzimuth, backAzimuth, and distance keys
     */
    def inverse(Point pt1, Point pt2) {

        // Create GeodeticCalculator with the selected Ellipsoid
        def calc = new GeodeticCalculator(ellipsoid)

        // Set starting and ending geographic points
        calc.setStartingGeographicPoint(pt1.x, pt1.y)
        calc.setDestinationGeographicPoint(pt2.x, pt2.y)

        // Calculate the forward azimuth and the distance
        double forwardAzimuth = calc.azimuth
        double distance = calc.orthodromicDistance

        // Calculate the back azimuth
        calc.setStartingGeographicPoint(pt2.x, pt2.y)
        calc.setDestinationGeographicPoint(pt1.x, pt1.y)
        double backAzimuth = calc.azimuth

        // Return a Map of results
        [
            forwardAzimuth: forwardAzimuth,
            backAzimuth: backAzimuth,
            distance: distance
        ]
    }
    
    /**
     * Place the given number of points between starting and ending Points
     * @param pt1 The start Point
     * @param pt2 The end Point
     * @param numberOfPoints The number of Points
     * @return A List of Points
     */
    List<Point> placePoints(Point pt1, Point pt2, int numberOfPoints) {

        // Create GeodeticCalculator with the selected Ellipsoid
        def calc = new GeodeticCalculator(ellipsoid)

        // Set starting and ending geographic points
        calc.setStartingGeographicPoint(pt1.x, pt1.y)
        calc.setDestinationGeographicPoint(pt2.x, pt2.y)

        // Calculate the forward azimuth and the distance
        double azimuth = calc.azimuth
        double distance = calc.orthodromicDistance
        double d = distance / (numberOfPoints + 1)
        
        // Create n number of points
        (1..numberOfPoints).collect{i ->
            // Set starting and ending geographic points
            calc.setStartingGeographicPoint(pt1.x, pt1.y)

            // Set the direction (azimuth and distance)
            calc.setDirection(azimuth, d * i)

            // Get the destination point
            def destinationPt = calc.destinationGeographicPoint
            new Point(destinationPt.x, destinationPt.y)
        }
    }
    
    /**
     * The string representation.
     * @return A string representation 
     */
    String toString() {
        return "Geodetic [${ellipsoid}]"
    }

