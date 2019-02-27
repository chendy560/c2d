package com.chendayu.dydoc;

import com.chendayu.dydoc.processor.*;
import com.chendayu.dydoc.testapp.controller.InheritDataController;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class InheritTest extends AbstractTest {

    @Test
    public void compileLombok() {
        Warehouse warehouse = compile(InheritDataController.class);
        Collection<Resource> resources = warehouse.getResources();
        assertThat(resources).hasSize(1);

        Resource resource = resources.iterator().next();
        assertThat(resource.getName()).isEqualTo("InheritData");

        SortedSet<Action> actions = resource.getActions();
        assertThat(actions).hasSize(1);

        Iterator<Action> iterator = actions.iterator();

        Action action = iterator.next();
        assertThat(action.getName()).isEqualTo("test");
        Property responseBody = action.getResponseBody();

        ObjectDeclaration responseDeclaration = (ObjectDeclaration) responseBody.getDeclaration();
        assertThat(responseDeclaration.getDescription()).isEqualTo(singletonList("子类"));

        List<Property> properties = responseDeclaration.getProperties();
        assertThat(properties).hasSize(4);

        Property nameProperty = properties.get(0);
        assertThat(nameProperty.getName()).isEqualTo("name");
        assertThat(nameProperty.getDescription()).isEqualTo(singletonList("姓名"));
        assertThat(nameProperty.getDeclaration().getType()).isEqualTo(DeclarationType.STRING);

        Property idProperty = properties.get(1);
        assertThat(idProperty.getName()).isEqualTo("id");
        assertThat(idProperty.getDescription()).isEqualTo(singletonList("id"));
        assertThat(idProperty.getDeclaration().getType()).isEqualTo(DeclarationType.STRING);

        Property ageProperty = properties.get(2);
        assertThat(ageProperty.getName()).isEqualTo("age");
        assertThat(ageProperty.getDescription()).isEqualTo(singletonList("年龄"));
        assertThat(ageProperty.getDeclaration().getType()).isEqualTo(DeclarationType.NUMBER);

        Property emailProperty = properties.get(3);
        assertThat(emailProperty.getName()).isEqualTo("email");
        assertThat(emailProperty.getDescription()).isEqualTo(singletonList("email"));
        assertThat(emailProperty.getDeclaration().getType()).isEqualTo(DeclarationType.STRING);
    }
}
