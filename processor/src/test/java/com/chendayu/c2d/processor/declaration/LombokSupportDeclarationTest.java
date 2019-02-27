package com.chendayu.c2d.processor.declaration;


import com.chendayu.c2d.processor.Declaration;
import com.chendayu.c2d.processor.DeclarationType;
import com.chendayu.c2d.processor.ObjectDeclaration;
import com.chendayu.c2d.processor.Property;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LombokSupportDeclarationTest extends AbstractDeclarationTest {

    @Test
    public void testDataAnnotation() {
        List<Declaration> result = compile(LombokTestClasses.class);

        ObjectDeclaration dataTest = (ObjectDeclaration) result.get(0);
        assertThat(dataTest.getDescription()).isEqualTo(Collections.singletonList("Data注解测试数据"));
        List<Property> dataProperties = dataTest.getProperties();
        assertThat(dataProperties).hasSize(2);

        checkProperty(dataProperties.get(0), "name", DeclarationType.STRING, Collections.singletonList("名字"));
        checkProperty(dataProperties.get(1), "age", DeclarationType.NUMBER, Collections.singletonList("年龄"));

        ObjectDeclaration getterData = (ObjectDeclaration) result.get(1);
        assertThat(getterData.getDescription()).isEqualTo(Collections.singletonList("Getter注解测试数据"));
        List<Property> getterProperties = getterData.getProperties();
        assertThat(getterProperties).hasSize(2);

        checkProperty(getterProperties.get(0), "score", DeclarationType.NUMBER, Collections.singletonList("分数"));
        checkProperty(getterProperties.get(1), "passed", DeclarationType.BOOLEAN, Collections.singletonList("是否通过"));
    }
}
