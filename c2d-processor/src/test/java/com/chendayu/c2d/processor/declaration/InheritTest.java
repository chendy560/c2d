package com.chendayu.c2d.processor.declaration;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.chendayu.c2d.processor.property.Property;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InheritTest extends AbstractDeclarationTest {

    @Test
    public void testInherit() {
        List<Declaration> declarations = compile(InheritTestClasses.class);

        checkChild((NestedDeclaration) declarations.get(0));
        checkSimpleChild((NestedDeclaration) declarations.get(1));
    }

    private void checkChild(NestedDeclaration child) {
        assertThat(child.getDescription()).isEqualTo("子类");
        Collection<Property> properties = child.accessibleProperties();
        assertThat(properties).hasSize(4);

        Iterator<Property> iterator = properties.iterator();
        checkProperty(iterator.next(), "name", DeclarationType.STRING, "姓名");
        checkProperty(iterator.next(), "data", DeclarationType.STRING, "测试数据");
        checkProperty(iterator.next(), "id", DeclarationType.STRING, "用户id");
        checkProperty(iterator.next(), "age", DeclarationType.NUMBER, "年龄");
    }

    private void checkSimpleChild(NestedDeclaration simpleChild) {
        assertThat(simpleChild.getDescription()).isEqualTo("");
        Collection<Property> properties = simpleChild.accessibleProperties();
        assertThat(properties).hasSize(2);

        Iterator<Property> iterator = properties.iterator();
        checkProperty(iterator.next(), "name", DeclarationType.STRING, "姓名");
        checkProperty(iterator.next(), "id", DeclarationType.STRING, "id");
    }
}
