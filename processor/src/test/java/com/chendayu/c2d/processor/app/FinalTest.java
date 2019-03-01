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

    private static final String BY_ID_URL = "/sample/v1/users/{id}";

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

        assertThat(actions).hasSize(7);

        Iterator<Action> actionIterator = actions.iterator();

        Action createAction = actionIterator.next();
        checkCreateAction(createAction);

        Action deleteAction = actionIterator.next();
        checkDeleteAction(deleteAction);

        Action getAction = actionIterator.next();
        checkGetAction(getAction);

        Action listAction = actionIterator.next();
        checkListAction(listAction);

        Action overwriteAction = actionIterator.next();
        checkOverwriteAction(overwriteAction);

        Action searchAction = actionIterator.next();
        checkSearchAction(searchAction);

        Action updateAction = actionIterator.next();
        checkUpdateAction(updateAction);
    }

    private void checkDeleteAction(Action action) {
        assertThat(action.getName()).isEqualTo("delete");

        assertThat(action.getDescription()).isEqualTo(Collections.singletonList("删除用户"));

        assertThat(action.getMethod()).isEqualTo(HttpMethod.DELETE);

        assertThat(action.getPath()).isEqualTo(BY_ID_URL);

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

    private void checkGetAction(Action action) {
        assertThat(action.getName()).isEqualTo("get");

        assertThat(action.getDescription()).isEqualTo(Collections.singletonList("通过id获取用户"));

        assertThat(action.getMethod()).isEqualTo(HttpMethod.GET);

        assertThat(action.getPath()).isEqualTo(BY_ID_URL);

        List<Property> pathVariables = action.getPathVariables();
        assertThat(pathVariables).hasSize(1);

        Property id = pathVariables.get(0);
        checkProperty(id, "id", DeclarationType.NUMBER, "用户id");

        List<Property> urlParameters = action.getUrlParameters();
        assertThat(urlParameters).hasSize(1);

        Property showDeleted = urlParameters.get(0);
        checkProperty(showDeleted, "showDeleted", DeclarationType.BOOLEAN, "是否展示被删除的数据");

        assertThat(action.getRequestBody()).isNull();

        Property user = action.getResponseBody();
        checkProperty(user, null, DeclarationType.OBJECT, "指定id的用户数据");

        checkUser((ObjectDeclaration) user.getDeclaration());
    }

    private void checkListAction(Action action) {
        assertThat(action.getName()).isEqualTo("list");

        assertThat(action.getDescription()).isEqualTo(Collections.singletonList("列举用户"));

        assertThat(action.getMethod()).isEqualTo(HttpMethod.GET);

        assertThat(action.getPath()).isEqualTo("/sample/v1/users");

        List<Property> pathVariables = action.getPathVariables();
        assertThat(pathVariables).isEmpty();

        List<Property> urlParameters = action.getUrlParameters();
        assertThat(urlParameters).hasSize(2);

        Property p = urlParameters.get(0);
        checkProperty(p, "p", DeclarationType.NUMBER, "第几页");
        Property n = urlParameters.get(1);
        checkProperty(n, "n", DeclarationType.NUMBER, "返回多少条");

        assertThat(action.getRequestBody()).isNull();

        Property userPage = action.getResponseBody();
        checkProperty(userPage, null, DeclarationType.OBJECT, "分页数据");

        checkUserPage((ObjectDeclaration) userPage.getDeclaration());
    }

    private void checkOverwriteAction(Action action) {
        assertThat(action.getName()).isEqualTo("overwrite");

        assertThat(action.getDescription()).isEqualTo(Collections.singletonList("更新/覆盖用户数据"));

        assertThat(action.getMethod()).isEqualTo(HttpMethod.PUT);

        assertThat(action.getPath()).isEqualTo(BY_ID_URL);

        List<Property> pathVariables = action.getPathVariables();
        assertThat(pathVariables).hasSize(1);

        Property id = pathVariables.get(0);
        checkProperty(id, "id", DeclarationType.NUMBER, "用户id");

        List<Property> urlParameters = action.getUrlParameters();
        assertThat(urlParameters).isEmpty();

        Property updateRequest = action.getRequestBody();
        checkProperty(updateRequest, null, DeclarationType.OBJECT, "更新请求");

        checkUserUpdateRequest((ObjectDeclaration) updateRequest.getDeclaration());


        Property user = action.getResponseBody();
        checkProperty(user, null, DeclarationType.VOID);
    }

    private void checkSearchAction(Action action) {
        assertThat(action.getName()).isEqualTo("search");

        assertThat(action.getDescription()).isEqualTo(Collections.singletonList("搜索用户"));

        assertThat(action.getMethod()).isEqualTo(HttpMethod.GET);

        assertThat(action.getPath()).isEqualTo("/sample/v1/users/search");

        List<Property> pathVariables = action.getPathVariables();
        assertThat(pathVariables).isEmpty();

        List<Property> urlParameters = action.getUrlParameters();
        assertThat(urlParameters).hasSize(5);

        Property p = urlParameters.get(0);
        checkProperty(p, "p", DeclarationType.NUMBER, "第几页");
        Property n = urlParameters.get(1);
        checkProperty(n, "n", DeclarationType.NUMBER, "返回多少条");

        Property name = urlParameters.get(2);
        checkProperty(name, "name", DeclarationType.ARRAY, "搜索关键字，搜索用户名，可以指定多个");
        Property maxAge = urlParameters.get(3);
        checkProperty(maxAge, "maxAge", DeclarationType.NUMBER, "最大年龄");
        Property minAge = urlParameters.get(4);
        checkProperty(minAge, "minAge", DeclarationType.NUMBER, "最小年龄");


        assertThat(action.getRequestBody()).isNull();

        Property userPage = action.getResponseBody();
        checkProperty(userPage, null, DeclarationType.OBJECT, "搜索结果的分页");

        checkUserPage((ObjectDeclaration) userPage.getDeclaration());
    }

    private void checkUserPage(ObjectDeclaration objectDeclaration) {
        assertThat(objectDeclaration.getName()).isEqualTo("Page");
        assertThat(objectDeclaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.Page");
        assertThat(objectDeclaration.getDescription()).isEqualTo(Collections.singletonList("数据分页的一页"));

        List<Property> typeParameters = objectDeclaration.getTypeParameters();
        assertThat(typeParameters).hasSize(1);
        Property t = typeParameters.get(0);
        checkProperty(t, "T", DeclarationType.TYPE_PARAMETER, "分页中的数据的类型");

        List<Declaration> typeArgs = objectDeclaration.getTypeArgs();
        assertThat(typeArgs).hasSize(1);
        Declaration user = typeArgs.get(0);
        checkUser((ObjectDeclaration) user);

        Collection<ObjectProperty> properties = objectDeclaration.getProperties();
        assertThat(properties).hasSize(3);

        Iterator<ObjectProperty> propertyIterator = properties.iterator();
        checkProperty(propertyIterator.next(), "count", DeclarationType.NUMBER, "数据总量");
        checkProperty(propertyIterator.next(), "currentPage", DeclarationType.NUMBER, "当前页");
        ObjectProperty items = propertyIterator.next();
        checkProperty(items, "items", DeclarationType.ARRAY, "本页数据");

        Declaration pageItemComponent = ((Declarations.ArrayDeclaration) items.getDeclaration()).getComponentType();
        Declarations.TypeArgDeclaration td = (Declarations.TypeArgDeclaration) pageItemComponent;
        assertThat(td.getType()).isEqualTo(DeclarationType.TYPE_PARAMETER);
        assertThat(td.getName()).isEqualTo("T");
    }

    private void checkUpdateAction(Action action) {
        assertThat(action.getName()).isEqualTo("update");

        assertThat(action.getDescription()).isEqualTo(Collections.singletonList("局部更新用户数据"));

        assertThat(action.getMethod()).isEqualTo(HttpMethod.PATCH);

        assertThat(action.getPath()).isEqualTo(BY_ID_URL);

        List<Property> pathVariables = action.getPathVariables();
        assertThat(pathVariables).hasSize(1);

        Property id = pathVariables.get(0);
        checkProperty(id, "id", DeclarationType.NUMBER, "用户id");

        List<Property> urlParameters = action.getUrlParameters();
        assertThat(urlParameters).isEmpty();

        Property userProperty = action.getRequestBody();
        checkProperty(userProperty, null, DeclarationType.OBJECT, "用户数据");

        checkUser((ObjectDeclaration) userProperty.getDeclaration());


        Property user = action.getResponseBody();
        checkProperty(user, null, DeclarationType.VOID);
    }

    private void checkUserUpdateRequest(ObjectDeclaration objectDeclaration) {

        assertThat(objectDeclaration.getName()).isEqualTo("UserUpdateRequest");
        assertThat(objectDeclaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.UserUpdateRequest");
        assertThat(objectDeclaration.getDescription()).isEqualTo(Collections.singletonList("用户更新请求"));


        assertThat(objectDeclaration.getTypeParameters()).isEmpty();
        assertThat(objectDeclaration.getTypeArgs()).isEmpty();

        Collection<ObjectProperty> properties = objectDeclaration.getProperties();
        assertThat(properties).hasSize(2);

        Iterator<ObjectProperty> iterator = properties.iterator();
        ObjectProperty nonsense = iterator.next();
        checkProperty(nonsense, "nonsense", DeclarationType.TIMESTAMP, "又是一个纯粹测试用字段");

        ObjectProperty user = iterator.next();
        checkProperty(user, "user", DeclarationType.OBJECT, "用户数据");
        checkUser((ObjectDeclaration) user.getDeclaration());
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
