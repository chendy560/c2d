package com.chendayu.dydoc.processor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResourceInfoStore {

    private static final String BASE_PACKAGE = "com.chendayu.dydoc.generated";

    private static final String RESOURCES_PACKAGE = BASE_PACKAGE + ".resources";

    private final List<Resource> resources = new ArrayList<>();

    private final DocGenerator docGenerator = new DocGenerator();

    private final Map<String, String> resourceFileNameMap = new HashMap<>();

    private final Filer filer;

    public ResourceInfoStore(ProcessingEnvironment processingEnvironment) {
        this.filer = processingEnvironment.getFiler();
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public void write() {
        for (Resource resource : resources) {
            String name = resource.getName();
            if (resourceFileNameMap.containsKey(name)) {
                continue;
            }
            try {
                String fileName = "r" + resource.getHash() + ".adoc";
                resourceFileNameMap.put(name, fileName);
                FileObject file = filer.createResource(StandardLocation.CLASS_OUTPUT, RESOURCES_PACKAGE, fileName);
                try (OutputStream outputStream = file.openOutputStream()) {
                    outputStream.write(docGenerator.generate(resource).getBytes(UTF_8));
                }
            } catch (IOException e) {
                throw new IllegalStateException("failed write file: " + e.getMessage());
            }
        }
    }
}
