package com.chendayu.c2d.processor.declaration;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.chendayu.c2d.processor.property.Property;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LombokSupportDeclarationTest extends AbstractDeclarationTest {

    @Test
    public void testDataAnnotation() {
        List<Declaration> result = compile(LombokTestClasses.class);

        NestedDeclaration dataTest = (NestedDeclaration) result.get(0);
        assertThat(dataTest.getDescription()).isEqualTo("Data注解测试数据");

        Collection<Property> dataProperties = dataTest.allProperties();
        assertThat(dataProperties).hasSize(2);

        Iterator<Property> iterator = dataProperties.iterator();
        checkProperty(iterator.next(), "name", DeclarationType.STRING, "名字");
        checkProperty(iterator.next(), "age", DeclarationType.NUMBER, "年龄");

        NestedDeclaration getterData = (NestedDeclaration) result.get(1);
        assertThat(getterData.getDescription()).isEqualTo("Getter注解测试数据");
        Collection<Property> getterProperties = getterData.allProperties();
        assertThat(getterProperties).hasSize(2);

        Iterator<Property> getterIterator = getterProperties.iterator();
        checkProperty(getterIterator.next(), "score", DeclarationType.NUMBER, "分数");
        checkProperty(getterIterator.next(), "passed", DeclarationType.BOOLEAN, "是否通过");
    }
}
