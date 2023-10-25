package geoscript.workspace

import org.geotools.api.data.DataStore
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

    /**
     * The Memory WorkspaceFactory
     */
    static class Factory extends WorkspaceFactory<Memory> {

        @Override
        Map getParametersFromString(String str) {
            Map params = [:]
            if (str.equalsIgnoreCase("memory")) {
                params["type"] = "memory"
            } else {
                params = super.getParametersFromString(str)
            }
            params
        }

        @Override
        Memory create(Map params) {
            if (params.type && params.type.toString().equalsIgnoreCase("memory")) {
                create(new org.geotools.data.memory.MemoryDataStore())
            } else {
                super.create(params)
            }
        }

        @Override
        Memory create(DataStore dataStore) {
            Memory mem = null
            if (dataStore instanceof org.geotools.data.memory.MemoryDataStore) {
                mem = new Memory(dataStore)
            }
            mem
        }
    }
}