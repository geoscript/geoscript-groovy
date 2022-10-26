package geoscript.carto

import org.junit.jupiter.api.io.TempDir

class AbstractCartoTest {

    @TempDir
    private File folder

    boolean saveToTarget = false

    void draw(String name, int width, int height, Closure closure) {
        String fileName = "carto_${name}.png"
        File file = saveToTarget ? new File("target/${fileName}") : new File(folder, fileName)
        file.withOutputStream { OutputStream outputStream ->
            CartoBuilder cartoBuilder = new ImageCartoBuilder(new PageSize(width, height), ImageCartoBuilder.ImageType.PNG)
            closure.call(cartoBuilder)
            cartoBuilder.build(outputStream)
        }
    }

}
