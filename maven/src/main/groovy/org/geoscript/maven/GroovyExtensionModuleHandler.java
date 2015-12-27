package org.geoscript.maven;

import com.google.common.base.Joiner;
import org.apache.maven.plugin.assembly.filter.ContainerDescriptorHandler;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class GroovyExtensionModuleHandler implements ContainerDescriptorHandler {

    private boolean extensionModuleIsPresent = false;

    String moduleName;

    String moduleVersion;

    List<String> extensionClasses = new ArrayList<String>();

    List<String> staticExtensionClasses = new ArrayList<String>();

    //@Override
    public void finalizeArchiveCreation(Archiver archiver) throws ArchiverException {
        if (extensionModuleIsPresent) {
            Properties props = new Properties();
            props.setProperty("moduleName", moduleName);
            props.setProperty("moduleVersion", moduleVersion);
            props.setProperty("extensionClasses", getString(extensionClasses));
            props.setProperty("staticExtensionClasses", getString(staticExtensionClasses));
            File file = null;
            try {
                file = File.createTempFile("extension-module", ".tmp");
                file.deleteOnExit();
                OutputStream out = new FileOutputStream(file);
                props.store(out, "Groovy Extension Module");
                out.close();
            } catch (IOException ex) {
                throw new ArchiverException("Error writing file!");
            }
            if (file != null) {
                archiver.addFile(file, "META-INF/services/org.codehaus.groovy.runtime.ExtensionModule");
            }
        }
    }

    //@Override
    public void finalizeArchiveExtraction(UnArchiver unArchiver) throws ArchiverException {

    }

   // @Override
    public List getVirtualFiles() {
        if (extensionModuleIsPresent) {
            return Arrays.asList("META-INF/services/org.codehaus.groovy.runtime.ExtensionModule");
        } else {
            return new ArrayList();
        }
    }

    //@Override
    public boolean isSelected(FileInfo fileInfo) throws IOException {
        if (fileInfo.getName().endsWith("META-INF/services/org.codehaus.groovy.runtime.ExtensionModule")) {
            System.out.println("Found an extension module! " + fileInfo.getName());
            try {
                extensionModuleIsPresent = true;
                Properties props = new Properties();
                props.load(fileInfo.getContents());
                moduleName = props.getProperty("moduleName");
                moduleVersion = props.getProperty("moduleVersion");
                List<String> extClasses = getList(props.getProperty("extensionClasses"));
                for (String clazz : extClasses) {
                    if (!extensionClasses.contains(clazz)) {
                        extensionClasses.add(clazz);
                    }
                }
                List<String> staticExtClasses = getList(props.getProperty("staticExtensionClasses"));
                for (String clazz : staticExtClasses) {
                    if (!staticExtensionClasses.contains(clazz)) {
                        staticExtensionClasses.add(clazz);
                    }
                }
            } catch(Exception ex) {
                System.err.println("Error reading extension module!");
            }
            return false;
        } else {
            return true;
        }
    }

    private List<String> getList(String str) {
        return Arrays.asList(str.split(","));
    }

    private String getString(List<String> strings) {
        return Joiner.on(",").join(strings);
    }

}
