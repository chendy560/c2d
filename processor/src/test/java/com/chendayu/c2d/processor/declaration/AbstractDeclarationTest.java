package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.Declaration;
import com.chendayu.c2d.processor.DeclarationType;
import com.chendayu.c2d.processor.Property;
import com.chendayu.c2d.processor.support.TestCompiler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractDeclarationTest {

    private static final Pattern REPLACE_PATTERN = Pattern.compile("\\$");
    private static final String REPLACEMENT = ".";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private TestCompiler compiler;

    @Before
    public void createCompiler() throws IOException {
        this.compiler = new TestCompiler(this.temporaryFolder);
    }

    void checkProperty(Property property, String name, DeclarationType type, List<String> description) {
        assertThat(property).isNotNull();
        assertThat(property.getName()).isEqualTo(name);
        assertThat(property.getDeclaration().getType()).isEqualTo(type);
        assertThat(property.getDescription()).isEqualTo(description);
    }

    List<Declaration> compile(Class<?>... types) {
        DeclarationTestProcessor processor = new DeclarationTestProcessor();
        this.compiler.getTask(types).call(processor);
        return processor.getResult();
    }

    String getQualifiedName(Class clazz) {
        return REPLACE_PATTERN.matcher(clazz.getName()).replaceAll(REPLACEMENT);
    }
}
