package com.chendayu.c2d.processor.declaration;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.chendayu.c2d.processor.property.Property;

import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class InheritTest extends AbstractDeclarationTest {

    @Test
    public void testInherit() {
        List<Declaration> declarations = compile(InheritTestClasses.class);

        checkChild((NestedDeclaration) declarations.get(0));
        checkSimpleChild((NestedDeclaration) declarations.get(1));
    }

    private void checkChild(NestedDeclaration child) {
        assertThat(child.getDescription()).isEqualTo(Collections.singletonList("子类"));
        Collection<Property> properties = child.accessibleProperties();
        assertThat(properties).hasSize(4);

        Iterator<Property> iterator = properties.iterator();
        checkProperty(iterator.next(), "name", DeclarationType.STRING, Collections.singletonList("姓名"));
        checkProperty(iterator.next(), "data", DeclarationType.STRING, Collections.singletonList("测试数据"));
        checkProperty(iterator.next(), "id", DeclarationType.STRING, Collections.singletonList("用户id"));
        checkProperty(iterator.next(), "age", DeclarationType.NUMBER, Collections.singletonList("年龄"));
    }

    private void checkSimpleChild(NestedDeclaration simpleChild) {
        assertThat(simpleChild.getDescription()).isEqualTo(emptyList());
        Collection<Property> properties = simpleChild.accessibleProperties();
        assertThat(properties).hasSize(2);

        Iterator<Property> iterator = properties.iterator();
        checkProperty(iterator.next(), "name", DeclarationType.STRING, Collections.singletonList("姓名"));
        checkProperty(iterator.next(), "id", DeclarationType.STRING, Collections.singletonList("id"));
    }
}
