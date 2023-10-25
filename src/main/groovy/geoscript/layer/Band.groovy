package geoscript.layer

import org.geotools.coverage.GridSampleDimension
import org.geotools.coverage.TypeMap
import org.geotools.api.coverage.SampleDimensionType
import javax.measure.Unit
import java.awt.image.DataBuffer
import org.geotools.api.coverage.SampleDimension

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
    }

    /**
     * Create a new Band with minimum and maximum values
     * @param description The Band's description
     * @param min The minimum value
     * @param max The maximum value
     */
    Band(String description, double min, double max) {
        this(builder().description(description).minimum(min).maximum(max).build().dim)
    }

    /**
     * Create a new Band with minimum, maximum, and no data values
     * @param description The Band's description
     * @param min The minimum value
     * @param max The maximum value
     * @param noValue The no data value
     */
    Band(String description, double min, double max, double noValue) {
        this(builder().description(description).minimum(min).maximum(max).noDataValue(noValue).build().dim)
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
    Unit getUnit() {
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
     * Get the Band's description
     * @return The Band's description
     */
    String getDescription() {
        dim.description.toString()
    }

    /**
     * Get the string representation
     * @return The string representation
     */
    @Override
    String toString() {
        "${this.getDescription()}"
    }

    /**
     * Create a new Band Builder
     * @return A new Band Builder
     */
    static Builder builder() {
        new Builder()
    }

    /**
     * A Builder for fluently creating Bands
     */
    static private class Builder {

        private String description
        private String type = null
        private List<String> categories = []
        private List<Double> noDataValues = []
        private double minimum
        private double maximum
        private double scale = 1
        private double offset = 0
        private Unit unit = null

        Builder description(String description) {
            this.description = description
            this
        }

        /**
         * Set the type (UNSIGNED_1BIT,UNSIGNED_2BITS,UNSIGNED_4BITS,UNSIGNED_8BITS,
         * SIGNED_8BITS,UNSIGNED_16BITS,SIGNED_16BITS,UNSIGNED_32BITS,SIGNED_32BITS,
         * REAL_32BITS,REAL_64BITS)
         * @param type The type
         * @return This Builder
         */
        Builder type(String type) {
            this.type = type
            this
        }

        Builder noDataValues(List<Double> values) {
            if (values) {
                this.noDataValues.addAll(values)
            }
            this
        }

        Builder noDataValue(double value) {
            if (value) {
                this.noDataValues.add(value)
            }
            this
        }

        Builder minimum(double minimum) {
            this.minimum = minimum
            this
        }

        Builder maximum(double maximum) {
            this.maximum = maximum
            this
        }

        Builder scale(double scale) {
            this.scale = scale
            this
        }

        Builder offset(double offset) {
            this.offset = offset
            this
        }

        Builder unit(Unit unit) {
            this.unit = unit
            this
        }

        Band build() {
            new Band(getSampleDimension())
        }

        protected GridSampleDimension getSampleDimension() {
            new GridSampleDimension(
                description,
                type ? SampleDimensionType.valueOf(type) : null,
                null,
                null,
                categories ? categories as String[] : null,
                noDataValues ? noDataValues as double[] : null,
                minimum,
                maximum,
                scale,
                offset,
                unit
            )
        }
    }

}