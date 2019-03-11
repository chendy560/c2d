package com.chendayu.c2d.processor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class DocGenerator {

    private static final String C2D_PACKAGE = "c2d";
    private static final String FILE_NAME = "c2d.adoc";

    private final Filer filer;

    public DocGenerator(ProcessingEnvironment processingEnvironment) {
        this.filer = processingEnvironment.getFiler();
    }

    public void printDoc(Warehouse warehouse) {

        try (Writer writer = new BufferedWriter(filer.createResource(StandardLocation.CLASS_OUTPUT, C2D_PACKAGE,
                FILE_NAME).openWriter())) {

            DocWriter docWriter = new DocWriter(writer);
            docWriter.printDoc(warehouse);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
