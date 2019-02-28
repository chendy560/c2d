package com.chendayu.c2d.processor.app;

import com.chendayu.c2d.processor.*;
import com.chendayu.c2d.processor.support.TestCompiler;
import com.chendayu.c2d.processor.support.TestSpringWebAnnotationProcessor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class FinalTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private TestCompiler compiler;

    @Before
    public void createCompiler() throws IOException {
        this.compiler = new TestCompiler(this.temporaryFolder);
    }

    private Warehouse compile(Class<?>... types) {
        TestSpringWebAnnotationProcessor processor = new TestSpringWebAnnotationProcessor();
        this.compiler.getTask(types).call(processor);
        return processor.getWarehouse();
    }

    @Test
    public void testAll() {
        Warehouse warehouse = compile(Page.class, PageRequest.class,
                BaseController.class, UserController.class,
                IdEntity.class,
                Pet.class, PetType.class, User.class,
                UserCreateRequest.class, UserSearchRequest.class, UserUpdateRequest.class);

        Collection<Resource> resources = warehouse.getResources();

        assertThat(resources).hasSize(1);

        Resource user = resources.iterator().next();
        SortedSet<Action> actions = user.getActions();

        assertThat(actions).hasSize(6);

        Iterator<Action> actionIterator = actions.iterator();

        Action createAction = actionIterator.next();
        checkCreateAction(createAction);

        Action deleteAction = actionIterator.next();
        checkDeleteAction(deleteAction);


        System.out.println("Ha!");
    }

    private void checkDeleteAction(Action action) {
        assertThat(action.getName()).isEqualTo("delete");

        assertThat(action.getDescription()).isEqualTo(Collections.singletonList("删除用户"));

        assertThat(action.getMethod()).isEqualTo(HttpMethod.DELETE);

        assertThat(action.getPath()).isEqualTo("/sample/v1/users/{id}");

        List<Property> pathVariables = action.getPathVariables();
        assertThat(pathVariables).hasSize(1);
        checkProperty(pathVariables.get(0), "id", DeclarationType.NUMBER, "用户id");

        assertThat(action.getRequestBody()).isNull();

        checkProperty(action.getResponseBody(), null, DeclarationType.VOID);
    }

    private void checkCreateAction(Action action) {
        assertThat(action.getName()).isEqualTo("create");

        assertThat(action.getDescription()).isEqualTo(Collections.singletonList("创建用户"));

        assertThat(action.getMethod()).isEqualTo(HttpMethod.POST);

        assertThat(action.getPath()).isEqualTo("/sample/v1/users");

        assertThat(action.getPathVariables()).isEmpty();

        List<Property> urlParameters = action.getUrlParameters();
        assertThat(urlParameters).hasSize(1);
        Property token = urlParameters.get(0);
        checkProperty(token, "token", DeclarationType.STRING, "测试用字段，假装自己是一个token");

        Property userCreateRequest = action.getRequestBody();
        checkProperty(userCreateRequest, null, DeclarationType.OBJECT, "用户创建请求");
        ObjectDeclaration userCreateRequestDeclaration = (ObjectDeclaration) userCreateRequest.getDeclaration();

        checkUserCreateRequest(userCreateRequestDeclaration);

        Property user = action.getResponseBody();
        checkUser(((ObjectDeclaration) user.getDeclaration()));
    }


    private void checkUserCreateRequest(ObjectDeclaration objectDeclaration) {

        assertThat(objectDeclaration.getName()).isEqualTo("UserCreateRequest");
        assertThat(objectDeclaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.UserCreateRequest");
        assertThat(objectDeclaration.getDescription()).isEqualTo(Collections.singletonList("用户创建请求"));


        assertThat(objectDeclaration.getTypeParameters()).isEmpty();
        assertThat(objectDeclaration.getTypeArgs()).isEmpty();

        Collection<ObjectProperty> properties = objectDeclaration.getProperties();
        assertThat(properties).hasSize(2);

        Iterator<ObjectProperty> iterator = properties.iterator();
        ObjectProperty nonsense = iterator.next();
        checkProperty(nonsense, "nonsense", DeclarationType.TIMESTAMP, "纯粹测试用字段");

        ObjectProperty user = iterator.next();
        checkProperty(user, "user", DeclarationType.OBJECT, "用户数据");
        checkUser((ObjectDeclaration) user.getDeclaration());
    }

    private void checkUser(ObjectDeclaration objectDeclaration) {

        assertThat(objectDeclaration.getName()).isEqualTo("User");
        assertThat(objectDeclaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.User");
        assertThat(objectDeclaration.getDescription()).isEqualTo(Collections.singletonList("用户，就是用户"));


        assertThat(objectDeclaration.getTypeParameters()).isEmpty();
        assertThat(objectDeclaration.getTypeArgs()).isEmpty();

        Collection<ObjectProperty> properties = objectDeclaration.getProperties();
        assertThat(properties).hasSize(5);

        Iterator<ObjectProperty> iterator = properties.iterator();
        checkId(iterator.next());

        ObjectProperty name = iterator.next();
        checkProperty(name, "username", DeclarationType.STRING, "用户名");

        ObjectProperty age = iterator.next();
        checkProperty(age, "age", DeclarationType.NUMBER, "年龄");

        ObjectProperty friends = iterator.next();
        checkProperty(friends, "friends", DeclarationType.ARRAY, "是坑爹的弗兰兹呢");
        Declaration componentType = ((Declarations.ArrayDeclaration) friends.getDeclaration()).getComponentType();
        assertThat(componentType).isSameAs(objectDeclaration);

        ObjectProperty pets = iterator.next();
        checkProperty(pets, "pets", DeclarationType.ARRAY, "宠物们");
        Declaration petComponentType = ((Declarations.ArrayDeclaration) pets.getDeclaration()).getComponentType();
        checkPet((ObjectDeclaration) petComponentType);
    }

    private void checkId(Property id) {
        checkProperty(id, "id", DeclarationType.NUMBER, "就是id", "没错就是id");
    }

    private void checkPet(ObjectDeclaration objectDeclaration) {
        assertThat(objectDeclaration.getName()).isEqualTo("Pet");
        assertThat(objectDeclaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.Pet");
        assertThat(objectDeclaration.getDescription()).isEqualTo(Collections.singletonList("宠物，可能是🐈，可能是🐶"));


        assertThat(objectDeclaration.getTypeParameters()).isEmpty();
        assertThat(objectDeclaration.getTypeArgs()).isEmpty();

        Collection<ObjectProperty> properties = objectDeclaration.getProperties();
        assertThat(properties).hasSize(5);

        Iterator<ObjectProperty> iterator = properties.iterator();
        checkId(iterator.next());

        ObjectProperty name = iterator.next();
        checkProperty(name, "name", DeclarationType.STRING, "宠物的名字");

        ObjectProperty birthday = iterator.next();
        checkProperty(birthday, "birthday", DeclarationType.TIMESTAMP, "宠物的生日，啊为什么是时间戳呢，因为现在还没有日期类型啊");

        ObjectProperty age = iterator.next();
        checkProperty(age, "age", DeclarationType.NUMBER, "年龄");

        ObjectProperty type = iterator.next();
        checkProperty(type, "type", DeclarationType.ENUM, "类型");

        EnumDeclaration types = (EnumDeclaration) type.getDeclaration();
        checkPetType(types);
    }

    private void checkPetType(EnumDeclaration declaration) {
        assertThat(declaration.getName()).isEqualTo("PetType");
        assertThat(declaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.PetType");
        assertThat(declaration.getDescription()).isEqualTo(Collections.singletonList("宠物类型，有点莫名"));

        List<Property> constants = declaration.getConstants();
        assertThat(constants).hasSize(2);

        Property cat = constants.get(0);
        checkProperty(cat, "CAT", DeclarationType.ENUM_CONST, "没错，就是🐈");

        Property dog = constants.get(1);
        checkProperty(dog, "DOG", DeclarationType.ENUM_CONST, "没错，就是🐶");
    }


    private void checkProperty(Property property, String name, DeclarationType type, String... description) {
        assertThat(property).isNotNull();
        assertThat(property.getName()).isEqualTo(name);
        assertThat(property.getDeclaration().getType()).isEqualTo(type);
        assertThat(property.getDescription()).isEqualTo(Arrays.asList(description));
    }
}
