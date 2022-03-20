package geoscript.style

import geoscript.style.io.Readers
import org.apache.commons.io.FilenameUtils

/**
 * A StyleRepository that uses a nested directory structure.  There
 * is a directory for each layer which contains a file for each style.
 * @author Jared Erickson
 */
class NestedDirectoryStyleRepository implements StyleRepository {

    private final File directory

    NestedDirectoryStyleRepository(File directory) {
        this.directory = directory
    }

    @Override
    String getDefaultForLayer(String layerName) {
        getForLayer(layerName, layerName)
    }

    @Override
    String getForLayer(String layerName, String styleName) {
        File layerDirectory = new File(directory, layerName)
        if (layerDirectory.exists()) {
            getStyleAndContents(styleName, layerDirectory)?.contents
        } else {
            null
        }
    }

    @Override
    Style getDefaultStyleForLayer(String layerName) {
        getStyleForLayer(layerName, layerName)
    }

    @Override
    Style getStyleForLayer(String layerName, String styleName) {
        File layerDirectory = new File(directory, layerName)
        if (layerDirectory.exists()) {
            getStyleAndContents(styleName, layerDirectory)?.style
        } else {
            null
        }
    }

    private Map<String,Object> getStyleAndContents(String styleName, File layerDirectory) {
        ["sld", "css", "ysld"].findResult { String ext ->
            File file = new File(layerDirectory, "${styleName}.${ext}")
            if (file.exists()) {
                [style: Readers.find(ext).read(file.text), contents: file.text, type: ext]
            }
        } ?: [:]
    }

    @Override
    List<Map<String, Object>> getForLayer(String layerName) {
        File layerDirectory = new File(directory, layerName)
        if (layerDirectory.exists()) {
            layerDirectory.listFiles(new StyleFileNameFilter()).collect { File file ->
                String ext = FilenameUtils.getExtension(file.name)
                [
                        layerName: layerName,
                        styleName: FilenameUtils.getBaseName(file.name),
                        style    : Readers.find(ext).read(file.text),
                        styleStr: file.text,
                        type: ext
                ]
            }
        } else {
            null
        }
    }

    @Override
    List<Map<String, Object>> getAll() {
        List styles = []
        this.directory.listFiles(new DirectoryFileFilter()).each { File dir ->
            dir.listFiles(new StyleFileNameFilter()).each { File file ->
                String ext = FilenameUtils.getExtension(file.name)
                styles.add([
                        layerName: dir.name,
                        styleName: FilenameUtils.getBaseName(file.name),
                        style    : Readers.find(ext).read(file.text),
                        styleStr: file.text,
                        type: ext
                ])
            }
        }
        styles
    }

    @Override
    void save(String layerName, String styleName, String style, Map options = [:]) {
        File layerDirectory = new File(directory, layerName)
        layerDirectory.mkdir()
        File file = new File(layerDirectory, "${styleName}.${options.get('type','sld')}")
        file.text = style
    }

    @Override
    void delete(String layerName, String styleName) {
        File layerDirectory = new File(directory, layerName)
        if (layerDirectory.exists()) {
            ["sld", "css", "ysld"].each { String ext ->
                File file = new File(layerDirectory, "${styleName}.${ext}")
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }

    private static class StyleFileNameFilter implements FilenameFilter {
        @Override
        boolean accept(File dir, String name) {
            name.endsWith(".sld") || name.endsWith(".css") || name.endsWith(".ysld")
        }
    }

    private static class DirectoryFileFilter implements FileFilter {
        @Override
        boolean accept(File pathname) {
            pathname.isDirectory()
        }
    }

}
