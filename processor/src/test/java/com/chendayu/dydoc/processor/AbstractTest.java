package com.chendayu.dydoc.processor;

import com.chendayu.dydoc.processor.support.TestCompiler;
import com.chendayu.dydoc.processor.support.TestSpringWebAnnotationProcessor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

/**
 * 本项目的 ut 整体逻辑都差不多，所以就把公用的部分抽离出来了
 */
public class AbstractTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private TestCompiler compiler;

    @Before
    public void createCompiler() throws IOException {
        this.compiler = new TestCompiler(this.temporaryFolder);
    }

    Warehouse compile(Class<?>... types) {
        TestSpringWebAnnotationProcessor processor = new TestSpringWebAnnotationProcessor();
        this.compiler.getTask(types).call(processor);
        return processor.getWarehouse();
    }
}
