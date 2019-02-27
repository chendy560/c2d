package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.Declaration;
import com.chendayu.c2d.processor.DeclarationType;
import com.chendayu.c2d.processor.ObjectDeclaration;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericTest extends AbstractDeclarationTest {

    @Test
    public void testInherit() {
        List<Declaration> declarations = compile(GenericTestClasses.class);

        assertThat(declarations).hasSize(1);
        ObjectDeclaration objectDeclaration = (ObjectDeclaration) declarations.get(0);

        List<Declaration> typeArgs = objectDeclaration.getTypeArgs();

        assertThat(typeArgs).hasSize(1);
        Declaration typeArg0 = typeArgs.get(0);
        assertThat(typeArg0.getType()).isEqualTo(DeclarationType.STRING);
    }
}
