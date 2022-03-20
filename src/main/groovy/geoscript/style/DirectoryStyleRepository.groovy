package geoscript.style

import geoscript.style.io.Readers
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
        getStyleAndContentsForLayer(layerName, styleName).contents
    }

    @Override
    Style getDefaultStyleForLayer(String layerName) {
        getStyleForLayer(layerName, layerName)
    }

    @Override
    Style getStyleForLayer(String layerName, String styleName) {
        getStyleAndContentsForLayer(layerName, styleName).style
    }

    private Map<String,Object> getStyleAndContentsForLayer(String layerName, String styleName) {
        ["sld","css","ysld"].findResult { String ext ->
            File file = new File(this.directory,"${styleName}.${ext}")
            if (file.exists()) {
                [style: Readers.find(ext).read(file.text), contents: file.text, type: ext]
            }
        } ?: [:]
    }

    @Override
    List<Map<String, Object>> getForLayer(String layerName) {
        Map styleAndContents = getStyleAndContentsForLayer(layerName, layerName)
        if (styleAndContents.style) {
            [
                [
                    layerName: layerName,
                    styleName: layerName,
                    style: styleAndContents.style,
                    styleStr: styleAndContents.contents,
                    type: styleAndContents.type
                ]
            ]
        } else {
            []
        }
    }

    @Override
    List<Map<String, Object>> getAll() {
        this.directory.listFiles(new StyleFileNameFilter()).collect { File file ->
            String ext = FilenameUtils.getExtension(file.name)
            [
                    layerName: FilenameUtils.getBaseName(file.name),
                    styleName: FilenameUtils.getBaseName(file.name),
                    style: Readers.find(ext).read(file.text),
                    styleStr: file.text,
                    type: ext
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
        ["sld","css","ysld"].each { String ext ->
            File file = new File(directory, "${styleName}.${ext}")
            if (file.exists()) {
                file.delete()
            }
        }
    }

    private static class StyleFileNameFilter implements FilenameFilter {
        @Override
        boolean accept(File dir, String name) {
            name.endsWith(".sld") || name.endsWith(".css") || name.endsWith(".ysld")
        }
    }

}
