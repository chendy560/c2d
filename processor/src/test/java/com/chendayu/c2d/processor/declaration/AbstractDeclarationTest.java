package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.model.Property;
import com.chendayu.c2d.processor.support.TestCompiler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Arrays;
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

    void checkProperty(Property property, String name, DeclarationType type, String... description) {
        checkProperty(property, name, type, Arrays.asList(description));
    }

    void checkProperty(Property property, String name, DeclarationType type, List<String> description) {
        assertThat(property).isNotNull();
        assertThat(property.getDisplayName()).isEqualTo(name);
        assertThat(property.getDeclaration().getType()).isEqualTo(type);
        assertThat(property.getDescription()).isEqualTo(description);
    }

    List<Declaration> compile(Class<?>... types) {
        DeclarationTestProcessor processor = new DeclarationTestProcessor();
        this.compiler.getTask(types).call(processor);
        return processor.getResult();
    }
}
