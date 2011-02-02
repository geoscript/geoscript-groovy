package geoscript.raster

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
     * Get the string representation
     * @return The string representation
     */
    @Override
    String toString() {
        dim.description.toString()
    }
}