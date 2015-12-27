package org.geoscript.maven;

import org.apache.maven.plugin.assembly.filter.MetaInfServicesHandler;
import org.codehaus.groovy.runtime.m12n.MetaInfExtensionModule;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;

/**
 * Created by jericks on 11/28/15.
 */
public class MetaInfServicesExcludeGroovyExtensionModuleHandler extends MetaInfServicesHandler {
    @Override
    protected boolean fileMatches(FileInfo fileInfo) {
        if (fileInfo.getName().endsWith("META-INF/services/org.codehaus.groovy.runtime.ExtensionModule")) {
            return false;
        } else {
            return super.fileMatches(fileInfo);
        }
    }
}
