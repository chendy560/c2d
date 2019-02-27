package com.chendayu.dydoc.processor.declaration;


import com.chendayu.dydoc.processor.DeclarationType;
import com.chendayu.dydoc.processor.ObjectDeclaration;
import com.chendayu.dydoc.processor.Property;
import com.chendayu.dydoc.processor.Warehouse;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LombokSupportDeclarationTest extends AbstractDeclarationTest {

    @Test
    public void testDataAnnotation() {
        Warehouse warehouse = compile(LombokTestClasses.class);
        ObjectDeclaration dataTest = warehouse.getDeclaration("com.chendayu.dydoc.processor.declaration.LombokTestClasses.DataTestClass");
        assertThat(dataTest.getDescription()).isEqualTo(Collections.singletonList("Data注解测试数据"));
        List<Property> properties = dataTest.getProperties();
        assertThat(properties).hasSize(2);

        checkProperty(properties.get(0), "name", DeclarationType.STRING, Collections.singletonList("名字"));
        checkProperty(properties.get(1), "age", DeclarationType.NUMBER, Collections.singletonList("年龄"));
    }

    @Test
    public void testGetterAnnotation() {
        Warehouse warehouse = compile(LombokTestClasses.class);
        ObjectDeclaration dataTest = warehouse.getDeclaration("com.chendayu.dydoc.processor.declaration.LombokTestClasses.GetterTestClass");
        assertThat(dataTest.getDescription()).isEqualTo(Collections.singletonList("Getter注解测试数据"));
        List<Property> properties = dataTest.getProperties();
        assertThat(properties).hasSize(2);

        checkProperty(properties.get(0), "score", DeclarationType.NUMBER, Collections.singletonList("分数"));
        checkProperty(properties.get(1), "passed", DeclarationType.BOOLEAN, Collections.singletonList("是否通过"));
    }
}
