package geoscript.raster

import javax.media.jai.Histogram as JaiHistogram

/**
 * A Histogram.
 * @author Jared Erickson
 */
class Histogram {

    /**
     * The wrapped javax.media.jai.Histogram
     */
    JaiHistogram histo

    /**
     * Create a new Histogram wrapping the javax.media.jai.Histogram
     * @param histo A javax.media.jai.Histogram
     */
    Histogram(JaiHistogram histo) {
        this.histo = histo
    }

    /**
     * Get the number of bands
     * @return The number of bands
     */
    int getNumberOfBands() {
        this.histo.numBands
    }

    /**
     * Get the i'th bin for the given band
     * @param i The bin number
     * @param band The band (defaults to 0)
     * @return A List of the bin's range
     */
    List bin(int i, int band=0) {
        if (i < histo.getNumBins(band)) {
          return [
            histo.getBinLowValue(band, i),
            histo.getBinLowValue(band, i + 1)
          ]
        } else {
            return []
        }
    }

    /**
     * Get a List of all bins
     * @param band The band
     * @return A List of all bins
     */
    List bins(int band = 0) {
        (0..<histo.getNumBins(band)).collect{ i ->
            bin(i, band)
        }
    }

    /**
     * Get the count for the i'th bin for the given band
     * @param i The bin bumber
     * @param band The band (defaults to 0)
     * @return The count
     */
    int count(int i, int band = 0) {
        histo.getBinSize(band, i)
    }

    /**
     * Get a List of all counts for all of the bins for the given band
     * @param band The band (defaults to 0)
     * @return A List of all counts
     */
    List counts(int band = 0) {
        (0..<histo.getNumBins(band)).collect{ i->
            count(i, band)
        }
    }

    /**
     * Get the count for the i'th bin for the first band
     * @param i The bin bumber
     * @return The count
     */
    int getAt(int i) {
        count(i)
    }

    /**
     * Get a List of all counts for all of the bins for the given band
     * @param values A List containing the bin and band
     * @return The count
     */
    int getAt(List values) {
        count(values[0],values[1])
    }
}
