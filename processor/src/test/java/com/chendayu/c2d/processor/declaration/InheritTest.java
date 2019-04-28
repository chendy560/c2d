package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.property.ObjectProperty;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class InheritTest extends AbstractDeclarationTest {

    @Test
    public void testInherit() {
        List<Declaration> declarations = compile(InheritTestClasses.class);

        checkChild((ObjectDeclaration) declarations.get(0));
        checkSimpleChild((ObjectDeclaration) declarations.get(1));
    }

    private void checkChild(ObjectDeclaration child) {
        assertThat(child.getDescription()).isEqualTo(Collections.singletonList("子类"));
        Collection<ObjectProperty> properties = child.getProperties();
        assertThat(properties).hasSize(4);

        Iterator<ObjectProperty> iterator = properties.iterator();
        checkProperty(iterator.next(), "name", DeclarationType.STRING, Collections.singletonList("姓名"));
        checkProperty(iterator.next(), "id", DeclarationType.STRING, Collections.singletonList("用户id"));
        checkProperty(iterator.next(), "age", DeclarationType.NUMBER, Collections.singletonList("年龄"));
        checkProperty(iterator.next(), "data", DeclarationType.STRING, Collections.singletonList("测试数据"));
    }

    private void checkSimpleChild(ObjectDeclaration simpleChild) {
        assertThat(simpleChild.getDescription()).isEqualTo(emptyList());
        Collection<ObjectProperty> properties = simpleChild.getProperties();
        assertThat(properties).hasSize(2);

        Iterator<ObjectProperty> iterator = properties.iterator();
        checkProperty(iterator.next(), "name", DeclarationType.STRING, Collections.singletonList("姓名"));
        checkProperty(iterator.next(), "id", DeclarationType.STRING, Collections.singletonList("id"));
    }
}
