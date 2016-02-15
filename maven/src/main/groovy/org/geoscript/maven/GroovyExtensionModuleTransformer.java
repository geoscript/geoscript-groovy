package org.geoscript.maven;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * A Maven Shade Transformer that correctly groups together multiple Groovy Extension Modules.
 * This transformer must be used with the ServiceResourceExcludingGroovyExtensionModuleTransformer.
 */
public class GroovyExtensionModuleTransformer implements ResourceTransformer {

    private String path = "META-INF/services/org.codehaus.groovy.runtime.ExtensionModule";

    String moduleName = "shaded-groovy-extension-module";

    String moduleVersion = "1.0";

    List<String> extensionClasses = new ArrayList<String>();

    List<String> staticExtensionClasses = new ArrayList<String>();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }

    public boolean canTransformResource(String resource) {
        return resource.startsWith(path);
    }

    public void processResource(String s, InputStream inputStream, List<Relocator> list) throws IOException {
        Properties props = new Properties();
        props.load(inputStream);
        List<String> extClasses = Lists.newArrayList(Splitter.on(",").split(props.getProperty("extensionClasses","")));
        for (String clazz : extClasses) {
            if (!extensionClasses.contains(clazz)) {
                extensionClasses.add(clazz);
            }
        }
        List<String> staticExtClasses = Lists.newArrayList(Splitter.on(",").split(props.getProperty("staticExtensionClasses", "")));
        for (String clazz : staticExtClasses) {
            if (!staticExtensionClasses.contains(clazz)) {
                staticExtensionClasses.add(clazz);
            }
        }
    }

    public boolean hasTransformedResource() {
        return !extensionClasses.isEmpty() || !staticExtensionClasses.isEmpty();
    }

    public void modifyOutputStream(JarOutputStream jarOutputStream) throws IOException {
        Properties props = new Properties();
        props.setProperty("moduleName", moduleName);
        props.setProperty("moduleVersion", moduleVersion);
        props.setProperty("extensionClasses", Joiner.on(",").join(extensionClasses));
        props.setProperty("staticExtensionClasses", Joiner.on(",").join(staticExtensionClasses));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        props.store(baos, "");
        baos.close();
        jarOutputStream.putNextEntry(new JarEntry(path));
        IOUtils.copy(new ByteArrayInputStream(baos.toByteArray()), jarOutputStream);
        jarOutputStream.closeEntry();
    }

}
