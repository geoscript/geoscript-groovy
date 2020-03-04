package geoscript.carto

class CartoFactories {

    static List<CartoFactory> list() {
        ServiceLoader.load(CartoFactory).iterator().collect()
    }

    static CartoFactory findByName(String name) {
        list().find{ CartoFactory cartoBuilderFactory ->
            cartoBuilderFactory.name.equalsIgnoreCase(name)
        }
    }

    static CartoFactory findByMimeType(String mimeType) {
        list().find{ CartoFactory cartoBuilderFactory ->
            cartoBuilderFactory.mimeType.equalsIgnoreCase(mimeType)
        }
    }

}
