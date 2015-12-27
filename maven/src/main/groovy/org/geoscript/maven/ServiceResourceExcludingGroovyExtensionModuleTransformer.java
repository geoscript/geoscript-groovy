package org.geoscript.maven;

import org.apache.maven.plugins.shade.resource.ServicesResourceTransformer;

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
        return resource.startsWith(path) && !resource.endsWith("META-INF/services/org.codehaus.groovy.runtime.ExtensionModule");
    }
}
