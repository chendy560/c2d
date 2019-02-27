package com.chendayu.dydoc.processor.declaration;

import com.chendayu.dydoc.processor.DeclarationType;
import com.chendayu.dydoc.processor.ObjectDeclaration;
import com.chendayu.dydoc.processor.Property;
import com.chendayu.dydoc.processor.Warehouse;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

public class InheritTest extends AbstractDeclarationTest {

    @Test
    public void testInherit() {
        Warehouse warehouse = compile(InheritTestClasses.class);
        ObjectDeclaration child = warehouse.getDeclaration("com.chendayu.dydoc.processor.declaration.InheritTestClasses.Child");
        checkChild(child);

        ObjectDeclaration simpleChild = warehouse.getDeclaration("com.chendayu.dydoc.processor.declaration.InheritTestClasses.SimpleChild");
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
