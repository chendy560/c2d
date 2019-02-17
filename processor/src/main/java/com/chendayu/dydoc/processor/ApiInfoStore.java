package com.chendayu.dydoc.processor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.TreeMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ApiInfoStore {

    private static final String BASE_PACKAGE = "com.chendayu.dydoc.generated";

    private static final String RESOURCES_PACKAGE = BASE_PACKAGE + ".resources";

    private static final String OBJECTS_PACKAGE = BASE_PACKAGE + ".objects";

    private static final String PROJECT_NAME_KEY = "project.name";

    private final TreeMap<String, Resource> resources = new TreeMap<>();

    private final DocGenerator docGenerator = new DocGenerator();

    private final TreeMap<String, ObjectStruct> objects = new TreeMap<>();

    private final Filer filer;

    private Properties properties = new Properties();

    private boolean wrote;

    public ApiInfoStore(ProcessingEnvironment processingEnvironment) {
        this.filer = processingEnvironment.getFiler();
        initProperties();
    }

    public boolean containsResource(String name) {
        return resources.containsKey(name);
    }

    public void addResource(Resource resource) {
        String name = resource.getName();
        if (containsResource(name)) {
            throw new IllegalStateException("resource '" + name + "' already exists");
        }
        ObjectStruct objectStruct = objects.get(name);
        if (objectStruct != null) {
            resource.setDescription(objectStruct.getDescription());
        }
        resources.put(name, resource);
    }

    public ObjectStruct getObject(String name) {
        return objects.get(name);
    }

    public void addObject(ObjectStruct objectStruct) {
        String name = objectStruct.getName();
        if (getObject(name) != null) {
            throw new IllegalArgumentException("object '" + name + "' already exists");
        }
        Resource resource = resources.get(name);
        if (resource != null) {
            resource.setDescription(objectStruct.getDescription());
        }
        this.objects.put(name, objectStruct);
    }

    public void write() {
        if (wrote) {
            throw new IllegalStateException("doc ready wrote");
        }
        wrote = true;
        DocGenerator.Index index = new DocGenerator.Index();
        index.setProjectName(properties.getProperty(PROJECT_NAME_KEY));
        for (Resource resource : resources.values()) {
            String fileName = resource.getHash() + ".adoc";
            index.addResourceFile(fileName);
            String content = docGenerator.generateResourcePages(resource);
            write(RESOURCES_PACKAGE, fileName, content);
        }

        for (ObjectStruct objectStruct : objects.values()) {
            String fileName = objectStruct.getHash() + ".adoc";
            index.addObjectFile(fileName);
            String content = docGenerator.generateObjectPages(objectStruct);
            write(OBJECTS_PACKAGE, fileName, content);
        }
        String indexString = docGenerator.generateIndex(index);
        write(BASE_PACKAGE, "index.adoc", indexString);
    }

    private void write(String packageName, String fileName, String content) {
        try {
            FileObject file = filer.createResource(StandardLocation.CLASS_OUTPUT, packageName, fileName);
            try (OutputStream outputStream = file.openOutputStream()) {
                outputStream.write(content.getBytes(UTF_8));
            }
        } catch (IOException e) {
            throw new IllegalStateException("failed write file: " + e.getMessage());
        }
    }

    private void initProperties() {
        try {
            FileObject propertiesFile = filer.getResource(StandardLocation.CLASS_OUTPUT,
                    "", "dydoc.properties");
            if (propertiesFile != null) {
                properties.load(propertiesFile.openReader(false));
            }

        } catch (FileNotFoundException e) {
            // ignore
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
