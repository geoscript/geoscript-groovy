package geoscript.style

import org.apache.commons.io.FilenameUtils

/**
 * A StyleRepository that uses a flat directory structure.
 * Each file in the given directory is the default style for the layer.
 * @author Jared Erickson
 */
class DirectoryStyleRepository implements StyleRepository {

    private final File directory

    DirectoryStyleRepository(File directory) {
        this.directory = directory
    }

    @Override
    String getDefaultForLayer(String layerName) {
        getForLayer(layerName, layerName)
    }

    @Override
    String getForLayer(String layerName, String styleName) {
        ["sld","css"].findResult { String ext ->
            File file = new File(this.directory,"${styleName}.${ext}")
            if (file.exists()) {
                return file.text
            }
        }
    }

    @Override
    List<Map<String, String>> getForLayer(String layerName) {
        String defaultStyle = getDefaultForLayer(layerName)
        if (defaultStyle) {
            [
                [
                    layerName: layerName,
                    styleName: layerName,
                    style: defaultStyle
                ]
            ]
        } else {
            []
        }
    }

    @Override
    List<Map<String, String>> getAll() {
        this.directory.listFiles(new StyleFileNameFilter()).collect { File file ->
            [
                    layerName: FilenameUtils.getBaseName(file.name),
                    styleName: FilenameUtils.getBaseName(file.name),
                    style: file.text
            ]
        }
    }

    @Override
    void save(String layerName, String styleName, String style, Map options = [:]) {
        File file = new File(directory, "${styleName}.${options.get('type','sld')}")
        file.text = style
    }

    @Override
    void delete(String layerName, String styleName) {
        ["sld","css"].each { String ext ->
            File file = new File(directory, "${styleName}.${ext}")
            if (file.exists()) {
                file.delete()
            }
        }
    }

    private static class StyleFileNameFilter implements FilenameFilter {
        @Override
        boolean accept(File dir, String name) {
            name.endsWith(".sld") || name.endsWith(".css")
        }
    }

}
