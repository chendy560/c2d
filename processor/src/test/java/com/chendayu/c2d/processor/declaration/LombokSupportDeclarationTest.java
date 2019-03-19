package com.chendayu.c2d.processor.declaration;


import com.chendayu.c2d.processor.model.Declaration;
import com.chendayu.c2d.processor.model.DeclarationType;
import com.chendayu.c2d.processor.model.ObjectDeclaration;
import com.chendayu.c2d.processor.model.ObjectProperty;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LombokSupportDeclarationTest extends AbstractDeclarationTest {

    @Test
    public void testDataAnnotation() {
        List<Declaration> result = compile(LombokTestClasses.class);

        ObjectDeclaration dataTest = (ObjectDeclaration) result.get(0);
        assertThat(dataTest.getDescription()).isEqualTo(Collections.singletonList("Data注解测试数据"));

        Collection<ObjectProperty> dataProperties = dataTest.getProperties();
        assertThat(dataProperties).hasSize(2);

        Iterator<ObjectProperty> iterator = dataProperties.iterator();
        checkProperty(iterator.next(), "name", DeclarationType.STRING, Collections.singletonList("名字"));
        checkProperty(iterator.next(), "age", DeclarationType.NUMBER, Collections.singletonList("年龄"));

        ObjectDeclaration getterData = (ObjectDeclaration) result.get(1);
        assertThat(getterData.getDescription()).isEqualTo(Collections.singletonList("Getter注解测试数据"));
        Collection<ObjectProperty> getterProperties = getterData.getProperties();
        assertThat(getterProperties).hasSize(2);

        Iterator<ObjectProperty> getterIterator = getterProperties.iterator();
        checkProperty(getterIterator.next(), "score", DeclarationType.NUMBER, Collections.singletonList("分数"));
        checkProperty(getterIterator.next(), "passed", DeclarationType.BOOLEAN, Collections.singletonList("是否通过"));
    }
}
