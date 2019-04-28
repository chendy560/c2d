package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.model.ObjectProperty;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonTest extends AbstractDeclarationTest {

    @Test
    public void testJackson() {
        List<Declaration> declarations = compile(JacksonTestSupport.class);

        assertThat(declarations).hasSize(1);

        ObjectDeclaration declaration = (ObjectDeclaration) declarations.get(0);
        Collection<ObjectProperty> properties = declaration.getProperties();
        assertThat(properties).hasSize(1);

        ObjectProperty property = properties.iterator().next();
        checkProperty(property, "username", DeclarationType.STRING, "用户名");
    }
}
