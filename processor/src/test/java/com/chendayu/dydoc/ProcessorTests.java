package com.chendayu.dydoc;

import com.chendayu.dydoc.controller.EmptyController;
import com.chendayu.dydoc.processor.SpringWebAnnotationProcessor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class ProcessorTests {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private TestCompiler compiler;

    @Before
    public void createCompiler() throws IOException {
        this.compiler = new TestCompiler(this.temporaryFolder);
    }

    @Test
    public void compileEmpty() {
        compile(EmptyController.class);
    }

    private void compile(Class<?>... types) {
        SpringWebAnnotationProcessor processor = new SpringWebAnnotationProcessor();
        this.compiler.getTask(types).call(processor);
    }
}
