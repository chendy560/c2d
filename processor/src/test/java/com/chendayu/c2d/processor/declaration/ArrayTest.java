package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.model.Declaration;
import com.chendayu.c2d.processor.model.DeclarationType;
import com.chendayu.c2d.processor.model.Declarations;
import org.junit.Test;

import java.util.List;

import static com.chendayu.c2d.processor.model.DeclarationType.ARRAY;
import static com.chendayu.c2d.processor.model.DeclarationType.NUMBER;
import static com.chendayu.c2d.processor.model.DeclarationType.STRING;
import static org.assertj.core.api.Assertions.assertThat;

public class ArrayTest extends AbstractDeclarationTest {

    @Test
    public void testCollection() {
        List<Declaration> declarations = compile(ArrayTestSupport.class);

        assertThat(declarations).hasSize(5);
        Declaration intArray = declarations.get(0);
        checkArrayDeclaration(intArray, NUMBER);

        Declaration charArray = declarations.get(1);
        assertThat(charArray.getType()).isEqualTo(STRING);

        Declaration doubleArray = declarations.get(2);
        checkArrayDeclaration(doubleArray, NUMBER);

        Declaration stringArray = declarations.get(3);
        checkArrayDeclaration(stringArray, STRING);

        Declaration stringListArray = declarations.get(4);
        checkArrayDeclaration(stringListArray, ARRAY);

        Declarations.ArrayDeclaration stringList = (Declarations.ArrayDeclaration) stringListArray;
        checkArrayDeclaration(stringList.getComponentType(), STRING);

    }

    private void checkArrayDeclaration(Declaration declaration, DeclarationType componentType) {
        assertThat(declaration.getType()).isEqualTo(ARRAY);
        Declarations.ArrayDeclaration arrayDeclaration = (Declarations.ArrayDeclaration) declaration;
        Declaration ct = arrayDeclaration.getComponentType();
        assertThat(ct.getType()).isEqualTo(componentType);
    }
}
