package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.DeclarationType;
import com.chendayu.c2d.processor.ObjectDeclaration;
import com.chendayu.c2d.processor.Property;
import com.chendayu.c2d.processor.Warehouse;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class InheritTest extends AbstractDeclarationTest {

    @Test
    public void testInherit() {
        Warehouse warehouse = compile(InheritTestClasses.class);
        ObjectDeclaration child = warehouse.getDeclaration(getQualifiedName(InheritTestClasses.Child.class));
        checkChild(child);

        ObjectDeclaration simpleChild = warehouse.getDeclaration(getQualifiedName(InheritTestClasses.SimpleChild.class));
        checkSimpleChild(simpleChild);
    }

    private void checkChild(ObjectDeclaration child) {
        assertThat(child.getDescription()).isEqualTo(Collections.singletonList("子类"));
        List<Property> properties = child.getProperties();
        assertThat(properties).hasSize(4);

        checkProperty(properties.get(0), "name", DeclarationType.STRING, Collections.singletonList("姓名"));
        checkProperty(properties.get(1), "id", DeclarationType.STRING, Collections.singletonList("用户id"));
        checkProperty(properties.get(2), "age", DeclarationType.NUMBER, Collections.singletonList("年龄"));
        checkProperty(properties.get(3), "data", DeclarationType.STRING, Collections.singletonList("测试数据"));
    }

    private void checkSimpleChild(ObjectDeclaration simpleChild) {
        assertThat(simpleChild.getDescription()).isEqualTo(emptyList());
        List<Property> properties = simpleChild.getProperties();
        assertThat(properties).hasSize(2);

        checkProperty(properties.get(0), "name", DeclarationType.STRING, Collections.singletonList("姓名"));
        checkProperty(properties.get(1), "id", DeclarationType.STRING, Collections.singletonList("id"));
    }
}
