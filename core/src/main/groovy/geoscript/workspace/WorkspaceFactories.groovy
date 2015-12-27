package geoscript.workspace

/**
 * A Utility for getting a List of WorkspaceFactories
 * @author Jared Erickson
 */
class WorkspaceFactories {

    /**
     * Get a List of WorkspaceFactories
     * @return A List of WorkspaceFactories
     */
    public static List<WorkspaceFactory> list() {
        ServiceLoader.load(WorkspaceFactory.class).iterator().collect()
    }

}
