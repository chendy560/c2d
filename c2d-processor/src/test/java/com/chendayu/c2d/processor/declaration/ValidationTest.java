package com.chendayu.c2d.processor.declaration;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

import com.chendayu.c2d.processor.property.Property;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationTest extends AbstractDeclarationTest {

    @Test
    public void testJackson() {
        List<Declaration> declarations = compile(ValidationTestSupport.class);

        assertThat(declarations).hasSize(1);

        NestedDeclaration declaration = (NestedDeclaration) declarations.get(0);
        Collection<Property> properties = declaration.accessibleProperties();
        assertThat(properties).hasSize(1);

        Property property = properties.iterator().next();
        checkProperty(property, "username", DeclarationType.STRING, "用户名");
        List<Annotation> constraintAnnotations = property.getConstraintAnnotations();
        assertThat(constraintAnnotations).hasSize(1);
    }
}
