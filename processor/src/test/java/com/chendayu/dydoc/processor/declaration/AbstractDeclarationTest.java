package com.chendayu.dydoc.processor.declaration;

import com.chendayu.dydoc.processor.DeclarationType;
import com.chendayu.dydoc.processor.Property;
import com.chendayu.dydoc.processor.Warehouse;
import com.chendayu.dydoc.processor.support.TestCompiler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractDeclarationTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private TestCompiler compiler;

    @Before
    public void createCompiler() throws IOException {
        this.compiler = new TestCompiler(this.temporaryFolder);
    }

    void checkProperty(Property property, String name, DeclarationType type, List<String> descripion) {
        assertThat(property).isNotNull();
        assertThat(property.getName()).isEqualTo(name);
        assertThat(property.getDeclaration().getType()).isEqualTo(type);
        assertThat(property.getDescription()).isEqualTo(descripion);
    }

    Warehouse compile(Class<?>... types) {
        DeclarationTestProcessor processor = new DeclarationTestProcessor();
        this.compiler.getTask(types).call(processor);
        return processor.getWarehouse();
    }
}
