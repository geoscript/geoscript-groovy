package geoscript.layer

import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.coverage.grid.io.GridFormatFinder
import org.geotools.coverage.grid.io.UnknownFormat

/**
 * A FormatFactory creates a Format from an input source which is
 * usually a File, URL, or InputStream
 * @param < T > The type Format
 */
abstract class FormatFactory<T extends Format> {

    /**
     * Create a Format from the input source or return null.
     * @param input The input source which is usually a File, URL, or InputStream
     * @return A Format or null
     */
    T create(Object input) {
        // Support file names and string urls
        if (input instanceof String) {
            String str = input as String
            boolean isFileOrUrl = getFileExtensions().find { String ext ->
                str.endsWith(ext)
            }
            if (isFileOrUrl) {
                boolean isUrl = false
                try {
                    URL url = new URL(str)
                    isUrl = true
                } catch(MalformedURLException ex) {
                    // Do nothing, just means that it must be a file
                }
                if (isUrl) {
                    input = new URL(str)
                } else {
                    input = new File(str)
                }
            }
        }
        if(input instanceof File) {
            File file = input as File
            if (file.exists()) {
                AbstractGridFormat format = GridFormatFinder.findFormat(file)
                if (format == null || format instanceof UnknownFormat) {
                    return null
                }
                createFromFormat(format, file)
            } else {
                String ext = file.name.substring(file.name.lastIndexOf(".") + 1).toLowerCase()
                if (ext in getFileExtensions()) {
                    createFromFile(file)
                }
            }
        }  else {
            AbstractGridFormat format
            try {
                format = GridFormatFinder.findFormat(input)
            } catch(Exception ex) {
                // Move on
            }
            if (format == null || format instanceof UnknownFormat) {
                return null
            } else {
                createFromFormat(format, input)
            }
        }

    }

    /**
     * Get a List of the file extensions that this Format supports
     * @return A List of file extensions
     */
    protected List<String> getFileExtensions() {
        []
    }

    /**
     * Create a Format from the GeoTools AbstractGridFormat and input source if possible.  Otherwise return null.
     * @param gridFormat The GeoTools AbstractGridFormat
     * @param source The input source
     * @return The Format or null
     */
    protected Format createFromFormat(AbstractGridFormat gridFormat, Object source) {
        null
    }

    /**
     * Create a Format from the File.
     * @param file The File
     * @return The Format or null
     */
    protected Format createFromFile(File file) {
        null
    }

}
