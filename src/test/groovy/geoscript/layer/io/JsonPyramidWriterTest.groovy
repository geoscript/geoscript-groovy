package geoscript.layer.io

import geoscript.layer.Pyramid
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * The JsonPyramidWriter Unit Test
 * @author Jared Erickson
 */
class JsonPyramidWriterTest {

    @Test void write() {
        Pyramid p = Pyramid.createGlobalMercatorPyramid()
        String actual = new JsonPyramidWriter().write(p)
        String expected = """{
    "proj": "EPSG:3857",
    "bounds": {
        "minX": -2.0036395147881314E7,
        "minY": -2.0037471205137067E7,
        "maxX": 2.0036395147881314E7,
        "maxY": 2.003747120513706E7
    },
    "origin": "BOTTOM_LEFT",
    "tileSize": {
        "width": 256,
        "height": 256
    },
    "grids": [
        {
            "z": 0,
            "width": 1,
            "height": 1,
            "xres": 156412.0,
            "yres": 156412.0
        },
        {
            "z": 1,
            "width": 2,
            "height": 2,
            "xres": 78206.0,
            "yres": 78206.0
        },
        {
            "z": 2,
            "width": 4,
            "height": 4,
            "xres": 39103.0,
            "yres": 39103.0
        },
        {
            "z": 3,
            "width": 8,
            "height": 8,
            "xres": 19551.5,
            "yres": 19551.5
        },
        {
            "z": 4,
            "width": 16,
            "height": 16,
            "xres": 9775.75,
            "yres": 9775.75
        },
        {
            "z": 5,
            "width": 32,
            "height": 32,
            "xres": 4887.875,
            "yres": 4887.875
        },
        {
            "z": 6,
            "width": 64,
            "height": 64,
            "xres": 2443.9375,
            "yres": 2443.9375
        },
        {
            "z": 7,
            "width": 128,
            "height": 128,
            "xres": 1221.96875,
            "yres": 1221.96875
        },
        {
            "z": 8,
            "width": 256,
            "height": 256,
            "xres": 610.984375,
            "yres": 610.984375
        },
        {
            "z": 9,
            "width": 512,
            "height": 512,
            "xres": 305.4921875,
            "yres": 305.4921875
        },
        {
            "z": 10,
            "width": 1024,
            "height": 1024,
            "xres": 152.74609375,
            "yres": 152.74609375
        },
        {
            "z": 11,
            "width": 2048,
            "height": 2048,
            "xres": 76.373046875,
            "yres": 76.373046875
        },
        {
            "z": 12,
            "width": 4096,
            "height": 4096,
            "xres": 38.1865234375,
            "yres": 38.1865234375
        },
        {
            "z": 13,
            "width": 8192,
            "height": 8192,
            "xres": 19.09326171875,
            "yres": 19.09326171875
        },
        {
            "z": 14,
            "width": 16384,
            "height": 16384,
            "xres": 9.546630859375,
            "yres": 9.546630859375
        },
        {
            "z": 15,
            "width": 32768,
            "height": 32768,
            "xres": 4.7733154296875,
            "yres": 4.7733154296875
        },
        {
            "z": 16,
            "width": 65536,
            "height": 65536,
            "xres": 2.38665771484375,
            "yres": 2.38665771484375
        },
        {
            "z": 17,
            "width": 131072,
            "height": 131072,
            "xres": 1.193328857421875,
            "yres": 1.193328857421875
        },
        {
            "z": 18,
            "width": 262144,
            "height": 262144,
            "xres": 0.5966644287109375,
            "yres": 0.5966644287109375
        },
        {
            "z": 19,
            "width": 524288,
            "height": 524288,
            "xres": 0.29833221435546875,
            "yres": 0.29833221435546875
        }
    ]
}"""
        assertTrue(actual.startsWith(expected.substring(0, expected.indexOf('"bounds": {'))))
        assertTrue(actual.endsWith(expected.substring(expected.indexOf('"origin": "BOTTOM_LEFT"'))))
    }
}
