package geoscript.workspace

import org.junit.Test
import static org.junit.Assert.*

class WorkspaceTestCase {

    @Test void getParametersFromString() {
        String wfsUrl = "http://localhost:8080/geoserver/ows?service=wfs&version=1.1.0&request=GetCapabilities"
        Map params = Workspace.getParametersFromString(wfsUrl)
        assertEquals params["WFSDataStoreFactory:GET_CAPABILITIES_URL"], wfsUrl
    }

}