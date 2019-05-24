package com.chendayu.c2d.processor.declaration;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericTest extends AbstractDeclarationTest {

    @Test
    public void testInherit() {
        List<Declaration> declarations = compile(GenericTestClasses.class);

        assertThat(declarations).hasSize(1);
        NestedDeclaration nestedDeclaration = (NestedDeclaration) declarations.get(0);

        List<TypeVarDeclaration> typeParameters = nestedDeclaration.getTypeParameters();
        assertThat(typeParameters).hasSize(1);

        TypeVarDeclaration typeParameter = typeParameters.get(0);
        checkTypeVarDeclaration(typeParameter, "T", "数据类型");

        List<Declaration> typeArgs = nestedDeclaration.getTypeArguments();

        assertThat(typeArgs).hasSize(1);
        Declaration typeArg0 = typeArgs.get(0);
        assertThat(typeArg0.getType()).isEqualTo(DeclarationType.STRING);
    }

    private void checkTypeVarDeclaration(TypeVarDeclaration property, String name, String... description) {
        assertThat(property).isNotNull();
        assertThat(property.getName()).isEqualTo(name);
        assertThat(property.getDescription()).isEqualTo(Arrays.asList(description));
    }
}
