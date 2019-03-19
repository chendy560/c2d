package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.model.Declaration;
import com.chendayu.c2d.processor.model.DeclarationType;
import com.chendayu.c2d.processor.model.ObjectDeclaration;
import com.chendayu.c2d.processor.model.Property;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericTest extends AbstractDeclarationTest {

    @Test
    public void testInherit() {
        List<Declaration> declarations = compile(GenericTestClasses.class);

        assertThat(declarations).hasSize(1);
        ObjectDeclaration objectDeclaration = (ObjectDeclaration) declarations.get(0);

        List<Property> typeParameters = objectDeclaration.getTypeParameters();
        assertThat(typeParameters).hasSize(1);

        Property typeParameter = typeParameters.get(0);
        checkProperty(typeParameter, "T", DeclarationType.TYPE_PARAMETER, "数据类型");

        List<Declaration> typeArgs = objectDeclaration.getTypeArgs();

        assertThat(typeArgs).hasSize(1);
        Declaration typeArg0 = typeArgs.get(0);
        assertThat(typeArg0.getType()).isEqualTo(DeclarationType.STRING);
    }
}
