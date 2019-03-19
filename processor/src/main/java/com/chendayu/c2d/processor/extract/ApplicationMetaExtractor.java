package com.chendayu.c2d.processor.extract;

import com.chendayu.c2d.processor.Warehouse;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

public class ApplicationMetaExtractor extends InfoExtractor {

    private static final String APPLICATION = "Application";

    public ApplicationMetaExtractor(ProcessingEnvironment processingEnvironment, Warehouse warehouse) {
        super(processingEnvironment, warehouse);
    }

    public void extract(TypeElement typeElement) {

        String mainClassName = typeElement.getSimpleName().toString();
        String qualifiedName = typeElement.getQualifiedName().toString();
        String basePackage = qualifiedName.substring(0, qualifiedName.indexOf(mainClassName) - 1);
        warehouse.setBasePackage(basePackage);

        int lastApplicationIndex = mainClassName.lastIndexOf(APPLICATION);
        if (lastApplicationIndex <= 0) {
            return;
        }

        String applicationName = mainClassName.substring(0, lastApplicationIndex);

        StringBuilder builder = new StringBuilder();
        builder.append(applicationName.charAt(0));

        for (int i = 1; i < applicationName.length(); i++) {
            char c = applicationName.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.append(' ');
            }
            builder.append(c);
        }

        warehouse.setApplicationName(builder.toString());
    }
}
