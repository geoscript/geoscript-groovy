package geoscript.layer

import org.junit.Test
import static org.junit.Assert.*

/**
 * The NetCDF Test Case
 */
class NetCDFTestCase {

    @Test void read() {
        File file = new File(getClass().getClassLoader().getResource("O3-NO2.nc").toURI())
        NetCDF netcdf = new NetCDF(file)
        assertNotNull(netcdf)
        assertEquals "NetCDF", netcdf.name
    }

    @Test void getNames() {
        File file = new File(getClass().getClassLoader().getResource("O3-NO2.nc").toURI())
        NetCDF netcdf = new NetCDF(file)
        assertNotNull(netcdf)
        List names = netcdf.names
        println names
        assertEquals(2, names.size())
        assertTrue(names.contains("O3"))
        assertTrue(names.contains("NO2"))
        netcdf.names.each{ String name ->
            Raster raster = netcdf.read(name)
            assertNotNull raster
            assertNotNull raster.proj
            assertNotNull raster.bounds
            raster.dispose()
        }
    }
}
