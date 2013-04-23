package geoscript.layer

/**
 * A Property Layer.
 * <p>You can create a Property Layer by passing the .properties file:</p>
 * <p><blockquote><pre>
 * Property prop = new Property('states.properties')
 * </pre></blockquote></p>
 */
class Property extends Layer {

    /**
     * The Property File
     */
    private File file

    /**
     * Create a Property Layer from a File
     * @param file The Property file (*.properties)
     */
    Property(File file) {
        super(create(file.absoluteFile))
        this.file = file
    }

    /**
     * Create a Property Layer from a File
     * @param file The Property file (*.properties)
     */
    Property(String file) {
        this(new File(file))
    }

    /**
     * Get the Property's File
     * @return The Property's File
     */
    File getFile() {
        this.file
    }

    /**
     * Create a Property Layer form a File
     */
    private static Layer create(File file) {
        String fileName = file.name
        String name = fileName.substring(0, fileName.lastIndexOf('.'))
        return new Layer(name, new geoscript.workspace.Property(file.parentFile))
    }
}
