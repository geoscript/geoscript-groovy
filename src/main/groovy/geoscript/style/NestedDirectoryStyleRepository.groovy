package geoscript.style

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
            ["sld", "css"].findResult { String ext ->
                File file = new File(layerDirectory, "${styleName}.${ext}")
                if (file.exists()) {
                    return file.text
                }
            }
        } else {
            ""
        }
    }

    @Override
    List<Map<String, String>> getForLayer(String layerName) {
        File layerDirectory = new File(directory, layerName)
        if (layerDirectory.exists()) {
            layerDirectory.listFiles(new StyleFileNameFilter()).collect { File file ->
                [
                        layerName: layerName,
                        styleName: FilenameUtils.getBaseName(file.name),
                        style    : file.text
                ]
            }
        } else {
            []
        }
    }

    @Override
    List<Map<String, String>> getAll() {
        List styles = []
        this.directory.listFiles(new DirectoryFileFilter()).each { File dir ->
            dir.listFiles(new StyleFileNameFilter()).each { File file ->
                styles.add([
                        layerName: dir.name,
                        styleName: FilenameUtils.getBaseName(file.name),
                        style    : file.text
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
            ["sld", "css"].each { String ext ->
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
            name.endsWith(".sld") || name.endsWith(".css")
        }
    }

    private static class DirectoryFileFilter implements FileFilter {
        @Override
        boolean accept(File pathname) {
            pathname.isDirectory()
        }
    }

}
