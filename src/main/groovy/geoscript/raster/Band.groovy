package geoscript.raster

import org.geotools.coverage.TypeMap
import java.awt.image.DataBuffer
import org.opengis.coverage.SampleDimension

/**
 * A Band of Raster Data that represents a single band/channel/layer.
 * @author Jared Erickson
 */
class Band {

    /**
     * The wrapped GeoTools SampleDimension
     */
    SampleDimension dim

    /**
     * Create a new Band wrapping a GeoTools SampleDimension
     * @param dim The wrapped GeoTools SampleDimension
     */
    Band(SampleDimension dim) {
         this.dim = dim
        dim.getSampleDimensionType()
    }

    /**
     * Get the minimum value
     * @return The minimum value
     */
    double getMin() {
        dim.minimumValue
    }

    /**
     * Get the maximum value
     * @return The maximum value
     */
    double getMax() {
        dim.maximumValue
    }

    /**
     * Get a List of no data values
     * @return The List of no data values
     */
    List<Double> getNoData() {
        dim.noDataValues
    }

    /**
     * Check whether the value is a NODATA value
     * @param value The value
     * @return Whether the value is a NODATA value
     */
    boolean isNoData(double value) {
        (noData) ? noData.contains(value) : false
    }

    /**
     * Get the unit information
     * @return The unit information
     */
    def getUnit() {
        dim.units
    }

    /**
     * Get the scale
     * @return The scale
     */
    double getScale() {
        dim.scale
    }

    /**
     * Get the offset
     * @return The offset
     */
    double getOffset() {
        dim.offset
    }

    /**
     * Get the type of data
     * @return The type of data
     */
    String getType() {
        int type = TypeMap.getDataBufferType(dim.sampleDimensionType)
        if (type == DataBuffer.TYPE_BYTE) {
            return "byte"
        } else if (type == DataBuffer.TYPE_DOUBLE) {
            return "double"
        } else if (type == DataBuffer.TYPE_FLOAT) {
            return "float"
        } else if (type == DataBuffer.TYPE_INT) {
            return "int"
        } else if (type == DataBuffer.TYPE_SHORT) {
            return "short"
        } else if (type == DataBuffer.TYPE_USHORT) {
            return "short"
        } else {
            return "undefined"
        }
    }

    /**
     * Get the string representation
     * @return The string representation
     */
    @Override
    String toString() {
        dim.description.toString()
    }
}