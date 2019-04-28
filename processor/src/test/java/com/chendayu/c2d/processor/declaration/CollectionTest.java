package com.chendayu.c2d.processor.declaration;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionTest extends AbstractDeclarationTest {

    @Test
    public void testCollection() {
        List<Declaration> declarations = compile(CollectionTestSupport.class);

        assertThat(declarations).hasSize(4);
        Declaration stringCollection = declarations.get(0);
        checkArrayDeclaration(stringCollection, DeclarationType.STRING);

        Declaration intList = declarations.get(1);
        checkArrayDeclaration(intList, DeclarationType.NUMBER);

        Declaration boolSet = declarations.get(2);
        checkArrayDeclaration(boolSet, DeclarationType.BOOLEAN);

        Declaration unsupported = declarations.get(3);
        checkArrayDeclaration(unsupported, DeclarationType.UNKNOWN);
    }

    private void checkArrayDeclaration(Declaration declaration, DeclarationType componentType) {
        assertThat(declaration.getType()).isEqualTo(DeclarationType.ARRAY);
        Declarations.ArrayDeclaration arrayDeclaration = (Declarations.ArrayDeclaration) declaration;
        Declaration ct = arrayDeclaration.getComponentType();
        assertThat(ct.getType()).isEqualTo(componentType);
    }
}
