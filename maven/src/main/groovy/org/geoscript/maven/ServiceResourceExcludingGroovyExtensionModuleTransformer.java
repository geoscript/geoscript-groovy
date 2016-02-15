package org.geoscript.maven;

import org.apache.maven.plugins.shade.resource.ServicesResourceTransformer;

/**
 * A Maven Shade Transformer that excludes Groovy Extension modules (META-INF/services/org.codehaus.groovy.runtime.ExtensionModule)
 * so that they can be handled by the GroovyExtensionModuleTransformer.
 */
public class ServiceResourceExcludingGroovyExtensionModuleTransformer extends ServicesResourceTransformer {

    private String path = "META-INF/services";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean canTransformResource(String resource) {
        return resource.startsWith(path) && !resource.endsWith(path + "/org.codehaus.groovy.runtime.ExtensionModule");
    }
}
