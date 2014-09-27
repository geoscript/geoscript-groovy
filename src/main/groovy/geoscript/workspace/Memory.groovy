package geoscript.workspace

import org.geotools.data.memory.MemoryDataStore

/**
 * A Memory Workspace stores it's Layers in memory.
 * <p><blockquote><pre>
 * Memory mem = new Memory()
 * Layer layer = mem.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
 * layer.add([new Point(1,1), "one"])
 * layer.add([new Point(2,2), "two"])
 * layer.add([new Point(3,3), "three"])
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Memory extends Workspace {

    /**
     * Create a new Memory Workspace
     */
    Memory() {
        super(new MemoryDataStore())
    }

    /**
     * Create a new Memory Workspace from a GeoTools MemoryDataStore
     * @param ds The MemoryDataStore
     */
    Memory(MemoryDataStore ds) {
        super(ds)
    }

    /**
     * Get the format
     * @return The workspace format name
     */
    String getFormat() {
        return "Memory"
    }
}