package com.chendayu.dydoc;

import com.chendayu.dydoc.processor.*;
import com.chendayu.dydoc.testapp.controller.LombokTestController;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class LombokTest extends AbstractTest {

    @Test
    public void compileLombok() {
        Warehouse warehouse = compile(LombokTestController.class);
        Collection<Resource> resources = warehouse.getResources();
        assertThat(resources).hasSize(1);

        Resource resource = resources.iterator().next();
        assertThat(resource.getName()).isEqualTo("LombokTest");

        SortedSet<Action> actions = resource.getActions();
        assertThat(actions).hasSize(2);

        Iterator<Action> iterator = actions.iterator();

        Action firstAction = iterator.next();
        checkFirstAction(firstAction);

        Action secondAction = iterator.next();
        checkSecondAction(secondAction);
    }

    private void checkFirstAction(Action action) {
        assertThat(action.getName()).isEqualTo("test1");
        Property responseBody = action.getResponseBody();

        ObjectDeclaration responseDeclaration = (ObjectDeclaration) responseBody.getDeclaration();
        assertThat(responseDeclaration.getDescription()).isEqualTo(singletonList("测试数据"));

        List<Property> properties = responseDeclaration.getProperties();
        assertThat(properties).hasSize(2);

        Property nameProperty = properties.get(0);
        assertThat(nameProperty.getName()).isEqualTo("name");
        assertThat(nameProperty.getDescription()).isEqualTo(singletonList("姓名"));
        assertThat(nameProperty.getDeclaration().getType()).isEqualTo(DeclarationType.STRING);

        Property ageProperty = properties.get(1);
        assertThat(ageProperty.getName()).isEqualTo("age");
        assertThat(ageProperty.getDescription()).isEqualTo(singletonList("年龄"));
        assertThat(ageProperty.getDeclaration().getType()).isEqualTo(DeclarationType.NUMBER);
    }

    private void checkSecondAction(Action action) {
        assertThat(action.getName()).isEqualTo("test2");
        Property responseBody = action.getResponseBody();

        ObjectDeclaration responseDeclaration = (ObjectDeclaration) responseBody.getDeclaration();
        assertThat(responseDeclaration.getDescription()).isEqualTo(singletonList("另一个测试数据"));

        List<Property> properties = responseDeclaration.getProperties();
        assertThat(properties).hasSize(1);

        Property emailProperty = properties.get(0);
        assertThat(emailProperty.getName()).isEqualTo("email");
        assertThat(emailProperty.getDescription()).isEqualTo(singletonList("电子邮件地址"));
        assertThat(emailProperty.getDeclaration().getType()).isEqualTo(DeclarationType.STRING);
    }
}
