package com.chendayu.c2d.processor.resource;

import java.io.File;
import java.io.IOException;

import com.chendayu.c2d.processor.Warehouse;
import com.chendayu.c2d.processor.support.TestCompiler;
import com.chendayu.c2d.processor.support.TestSpringWebAnnotationProcessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

/**
 * 本项目的 ut 整体逻辑都差不多，所以就把公用的部分抽离出来了
 */
public class AbstractResourceTest {  // NOSONAR 这个不是个测试类，只是个抽象类，不跑测试

    @TempDir
    public File temporaryFolder;

    private TestCompiler compiler;

    @BeforeEach
    public void createCompiler() throws IOException {
        this.compiler = new TestCompiler(this.temporaryFolder);
    }

    Warehouse compile(Class<?>... types) {
        TestSpringWebAnnotationProcessor processor = new TestSpringWebAnnotationProcessor();
        this.compiler.getTask(types).call(processor);
        return processor.getWarehouse();
    }
}
