package geoscript

class FileUtil {

    static File createDir(File dir, String name) {
        File file = new File(dir, name)
        file.mkdir()
        file
    }

}
