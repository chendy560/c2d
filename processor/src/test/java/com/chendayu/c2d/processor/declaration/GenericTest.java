package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.property.Property;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericTest extends AbstractDeclarationTest {

    @Test
    public void testInherit() {
        List<Declaration> declarations = compile(GenericTestClasses.class);

        assertThat(declarations).hasSize(1);
        NestedDeclaration nestedDeclaration = (NestedDeclaration) declarations.get(0);

        List<Property> typeParameters = nestedDeclaration.getTypeParameters();
        assertThat(typeParameters).hasSize(1);

        Property typeParameter = typeParameters.get(0);
        checkProperty(typeParameter, "T", DeclarationType.TYPE_PARAMETER, "数据类型");

        List<Declaration> typeArgs = nestedDeclaration.getTypeArguments();

        assertThat(typeArgs).hasSize(1);
        Declaration typeArg0 = typeArgs.get(0);
        assertThat(typeArg0.getType()).isEqualTo(DeclarationType.STRING);
    }
}
