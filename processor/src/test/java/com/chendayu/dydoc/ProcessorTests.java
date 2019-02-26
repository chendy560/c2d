package com.chendayu.dydoc;

import com.chendayu.dydoc.processor.Warehouse;
import com.chendayu.dydoc.testapp.controller.EmptyController;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

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
        Warehouse warehouse = compile(EmptyController.class);
        assertThat(warehouse.getResources()).isEmpty();
    }

    private Warehouse compile(Class<?>... types) {
        TestSpringWebAnnotationProcessor processor = new TestSpringWebAnnotationProcessor();
        this.compiler.getTask(types).call(processor);
        return processor.getWarehouse();
    }
}
