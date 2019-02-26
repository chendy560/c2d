package com.chendayu.dydoc;

import com.chendayu.dydoc.processor.Resource;
import com.chendayu.dydoc.processor.Warehouse;
import com.chendayu.dydoc.testapp.controller.SimpleController;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class SimpleTests {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private TestCompiler compiler;

    @Before
    public void createCompiler() throws IOException {
        this.compiler = new TestCompiler(this.temporaryFolder);
    }

    @Test
    public void compileSimple() {
        Warehouse warehouse = compile(SimpleController.class);

        for (Resource resource : warehouse.getResources()) {
            System.out.println();
        }
    }

    private Warehouse compile(Class<?>... types) {
        TestSpringWebAnnotationProcessor processor = new TestSpringWebAnnotationProcessor();
        this.compiler.getTask(types).call(processor);
        return processor.getWarehouse();
    }
}
