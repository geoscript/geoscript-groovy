package geoscript.layer

abstract class TileLayerFactory<T extends TileLayer> {

    T create(String str) {
        Map params = [:]
        if (str.contains("=")) {
            str.split("[ ]+(?=([^\']*\'[^\']*\')*[^\']*\$)").each {
                def parts = it.split("=")
                def key = parts[0].trim()
                if ((key.startsWith("'") && key.endsWith("'")) ||
                        (key.startsWith("\"") && key.endsWith("\""))) {
                    key = key.substring(1, key.length() - 1)
                }
                def value = parts[1].trim()
                if ((value.startsWith("'") && value.endsWith("'")) ||
                        (value.startsWith("\"") && value.endsWith("\""))) {
                    value = value.substring(1, value.length() - 1)
                }
                params.put(key, value)
            }
        }
        create(params)
    }

    abstract T create(Map params)

    abstract TileRenderer getTileRenderer(Map options, TileLayer tileLayer, List<Layer> layers)

}
