package geoscript.workspace

import org.geotools.data.memory.MemoryDataStore

/**
 * A Memory Workspace
 */
class Memory extends Workspace {

    /**
     * Create a new Memory Workspace
     */
    Memory() {
        super(new MemoryDataStore())
    }

}