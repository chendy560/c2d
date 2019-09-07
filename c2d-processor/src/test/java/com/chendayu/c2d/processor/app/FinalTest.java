package com.chendayu.c2d.processor.app;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.chendayu.c2d.processor.Warehouse;
import com.chendayu.c2d.processor.action.Action;
import com.chendayu.c2d.processor.action.Resource;
import com.chendayu.c2d.processor.declaration.ArrayDeclaration;
import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationType;
import com.chendayu.c2d.processor.declaration.EnumDeclaration;
import com.chendayu.c2d.processor.declaration.NestedDeclaration;
import com.chendayu.c2d.processor.declaration.TypeVarDeclaration;
import com.chendayu.c2d.processor.property.Property;
import com.chendayu.c2d.processor.support.TestCompiler;
import com.chendayu.c2d.processor.support.TestSpringWebAnnotationProcessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;

public class FinalTest {

    private static final String BY_ID_URL = "/sample/v1/users/{id}";

    @TempDir
    public File temporaryFolder;

    private TestCompiler compiler;

    @BeforeEach
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
        Warehouse warehouse = compile(
                SimpleTestApplication.class,
                Page.class, PageRequest.class,
                BaseController.class, UserController.class,
                IdEntity.class,
                Pet.class, PetType.class, User.class,
                UserCreateRequest.class, UserSearchRequest.class, UserUpdateRequest.class);

        String applicationName = warehouse.getApplicationName();
        assertThat(applicationName).isEqualTo("Simple Test");
        String basePackage = warehouse.getBasePackage();
        assertThat(basePackage).isEqualTo("com.chendayu.c2d.processor.app.c2d");

        Collection<Resource> resources = warehouse.getResources();

        assertThat(resources).hasSize(1);

        Resource user = resources.iterator().next();
        Collection<Action> actions = user.getActions();

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
        assertThat(action.getName()).isEqualTo("Delete");

        assertThat(action.getDescription()).isEqualTo("删除用户");

        assertThat(action.getMethod()).isEqualTo(HttpMethod.DELETE);

        assertThat(action.getPath()).isEqualTo(BY_ID_URL);

        List<Property> pathVariables = action.getPathVariables();
        assertThat(pathVariables).hasSize(1);
        checkProperty(pathVariables.get(0), "id", DeclarationType.NUMBER, "用户id");

        assertThat(action.getRequestBody()).isNull();

        assertThat(action.getResponseBody()).isNull();
    }

    private void checkCreateAction(Action action) {
        assertThat(action.getName()).isEqualTo("Create");

        assertThat(action.getDescription()).isEqualTo("创建用户");

        assertThat(action.getMethod()).isEqualTo(HttpMethod.POST);

        assertThat(action.getPath()).isEqualTo("/sample/v1/users");

        assertThat(action.getPathVariables()).isEmpty();

        List<Property> urlParameters = action.getUrlParameters();
        assertThat(urlParameters).hasSize(1);
        Property token = urlParameters.get(0);
        checkProperty(token, "token", DeclarationType.STRING, "测试用字段，假装自己是一个token");

        Property userCreateRequest = action.getRequestBody();
        checkProperty(userCreateRequest, null, DeclarationType.OBJECT, "用户创建请求");
        NestedDeclaration userCreateRequestDeclaration = (NestedDeclaration) userCreateRequest.getDeclaration();

        checkUserCreateRequest(userCreateRequestDeclaration);

        Property user = action.getResponseBody();
        checkUser(((NestedDeclaration) user.getDeclaration()));
    }

    private void checkGetAction(Action action) {
        assertThat(action.getName()).isEqualTo("Get");

        assertThat(action.getDescription()).isEqualTo("通过id获取用户");

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

        checkUser((NestedDeclaration) user.getDeclaration());
    }

    private void checkListAction(Action action) {
        assertThat(action.getName()).isEqualTo("List");

        assertThat(action.getDescription()).isEqualTo("列举用户");

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

        checkUserPage((NestedDeclaration) userPage.getDeclaration());
    }

    private void checkOverwriteAction(Action action) {
        assertThat(action.getName()).isEqualTo("Overwrite");

        assertThat(action.getDescription()).isEqualTo("更新/覆盖用户数据");

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

        checkUserUpdateRequest((NestedDeclaration) updateRequest.getDeclaration());


        Property responseBody = action.getResponseBody();
        assertThat(responseBody).isNull();
    }

    private void checkSearchAction(Action action) {
        assertThat(action.getName()).isEqualTo("Search");

        assertThat(action.getDescription()).isEqualTo("搜索用户");

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

        checkUserPage((NestedDeclaration) userPage.getDeclaration());
    }

    private void checkUserPage(NestedDeclaration nestedDeclaration) {
        assertThat(nestedDeclaration.getShortName()).isEqualTo("Page");
        assertThat(nestedDeclaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.Page");
        assertThat(nestedDeclaration.getDescription()).isEqualTo("数据分页的一页");

        List<TypeVarDeclaration> typeParameters = nestedDeclaration.getTypeParameters();
        assertThat(typeParameters).hasSize(1);
        TypeVarDeclaration t = typeParameters.get(0);
        checkTypeVarDeclaration(t, "T", "分页中的数据的类型");

        List<Declaration> typeArgs = nestedDeclaration.getTypeArguments();
        assertThat(typeArgs).hasSize(1);
        Declaration user = typeArgs.get(0);
        checkUser((NestedDeclaration) user);

        Collection<Property> properties = nestedDeclaration.accessibleProperties();
        assertThat(properties).hasSize(3);

        Iterator<Property> propertyIterator = properties.iterator();
        checkProperty(propertyIterator.next(), "count", DeclarationType.NUMBER, "数据总量");
        checkProperty(propertyIterator.next(), "currentPage", DeclarationType.NUMBER, "当前页");
        Property items = propertyIterator.next();
        checkProperty(items, "items", DeclarationType.ARRAY, "本页数据");

//        Declaration pageItemComponent = ((ArrayDeclaration) items.getDeclaration()).getItemType();
//        TypeVarDeclaration td = (TypeVarDeclaration) pageItemComponent;
//        assertThat(td.getType()).isEqualTo(DeclarationType.TYPE_PARAMETER);
//        assertThat(td.getName()).isEqualTo("T");
    }

    private void checkUpdateAction(Action action) {
        assertThat(action.getName()).isEqualTo("Update");

        assertThat(action.getDescription()).isEqualTo("局部更新用户数据");

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

        checkUser((NestedDeclaration) userProperty.getDeclaration());


        Property responseBody = action.getResponseBody();
        assertThat(responseBody).isNull();
    }

    private void checkUserUpdateRequest(NestedDeclaration nestedDeclaration) {

        assertThat(nestedDeclaration.getShortName()).isEqualTo("UserUpdateRequest");
        assertThat(nestedDeclaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.UserUpdateRequest");
        assertThat(nestedDeclaration.getDescription()).isEqualTo("用户更新请求");


        assertThat(nestedDeclaration.getTypeParameters()).isEmpty();
        assertThat(nestedDeclaration.getTypeArguments()).isEmpty();

        Collection<Property> properties = nestedDeclaration.accessibleProperties();
        assertThat(properties).hasSize(2);

        Iterator<Property> iterator = properties.iterator();
        Property nonsense = iterator.next();
        checkProperty(nonsense, "nonsense", DeclarationType.TIMESTAMP, "又是一个纯粹测试用字段");

        Property user = iterator.next();
        checkProperty(user, "user", DeclarationType.OBJECT, "用户数据");
        checkUser((NestedDeclaration) user.getDeclaration());
    }

    private void checkUserCreateRequest(NestedDeclaration nestedDeclaration) {

        assertThat(nestedDeclaration.getShortName()).isEqualTo("UserCreateRequest");
        assertThat(nestedDeclaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.UserCreateRequest");
        assertThat(nestedDeclaration.getDescription()).isEqualTo("用户创建请求");


        assertThat(nestedDeclaration.getTypeParameters()).isEmpty();
        assertThat(nestedDeclaration.getTypeArguments()).isEmpty();

        Collection<Property> properties = nestedDeclaration.allProperties();
        assertThat(properties).hasSize(2);

        Iterator<Property> iterator = properties.iterator();
        Property nonsense = iterator.next();
        checkProperty(nonsense, "nonsense", DeclarationType.TIMESTAMP, "纯粹测试用字段");

        Property user = iterator.next();
        checkProperty(user, "user", DeclarationType.OBJECT, "用户数据");
        checkUser((NestedDeclaration) user.getDeclaration());
    }

    private void checkUser(NestedDeclaration nestedDeclaration) {

        assertThat(nestedDeclaration.getShortName()).isEqualTo("User");
        assertThat(nestedDeclaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.User");
        assertThat(nestedDeclaration.getDescription()).isEqualTo("用户，就是用户");


        assertThat(nestedDeclaration.getTypeParameters()).isEmpty();
        assertThat(nestedDeclaration.getTypeArguments()).isEmpty();

        Collection<Property> properties = nestedDeclaration.accessibleProperties();
        assertThat(properties).hasSize(5);

        Iterator<Property> iterator = properties.iterator();
        checkId(iterator.next());

        Property name = iterator.next();
        checkProperty(name, "username", DeclarationType.STRING, "用户名");

        Property age = iterator.next();
        checkProperty(age, "age", DeclarationType.NUMBER, "年龄");

        Property friends = iterator.next();
        checkProperty(friends, "friends", DeclarationType.ARRAY, "是坑爹的弗兰兹呢");
        Declaration componentType = ((ArrayDeclaration) friends.getDeclaration()).getItemType();
        assertThat(componentType).isSameAs(nestedDeclaration);

        Property pets = iterator.next();
        checkProperty(pets, "pets", DeclarationType.ARRAY, "宠物们");
        Declaration petComponentType = ((ArrayDeclaration) pets.getDeclaration()).getItemType();
        checkPet((NestedDeclaration) petComponentType);
    }

    private void checkId(Property id) {
        checkProperty(id, "id", DeclarationType.NUMBER, "就是id\n 没错就是id");
    }

    private void checkPet(NestedDeclaration nestedDeclaration) {
        assertThat(nestedDeclaration.getShortName()).isEqualTo("Pet");
        assertThat(nestedDeclaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.Pet");
        assertThat(nestedDeclaration.getDescription()).isEqualTo("宠物，可能是🐈，可能是🐶");


        assertThat(nestedDeclaration.getTypeParameters()).isEmpty();
        assertThat(nestedDeclaration.getTypeArguments()).isEmpty();

        Collection<Property> properties = nestedDeclaration.accessibleProperties();
        assertThat(properties).hasSize(5);

        Iterator<Property> iterator = properties.iterator();
        checkId(iterator.next());

        Property name = iterator.next();
        checkProperty(name, "name", DeclarationType.STRING, "宠物的名字");

        Property birthday = iterator.next();
        checkProperty(birthday, "birthday", DeclarationType.TIMESTAMP, "宠物的生日，啊为什么是时间戳呢，因为现在还没有日期类型啊");

        Property age = iterator.next();
        checkProperty(age, "age", DeclarationType.NUMBER, "年龄");

        Property type = iterator.next();
        checkProperty(type, "type", DeclarationType.ENUM, "类型");

        EnumDeclaration types = (EnumDeclaration) type.getDeclaration();
        checkPetType(types);
    }

    private void checkPetType(EnumDeclaration declaration) {
        assertThat(declaration.getName()).isEqualTo("PetType");
        assertThat(declaration.getQualifiedName()).isEqualTo("com.chendayu.c2d.processor.app.PetType");
        assertThat(declaration.getDescription()).isEqualTo("宠物类型，有点莫名");

        List<Property> constants = declaration.getConstants();
        assertThat(constants).hasSize(2);

        Property cat = constants.get(0);
        checkProperty(cat, "CAT", DeclarationType.ENUM_CONST, "没错，就是🐈");

        Property dog = constants.get(1);
        checkProperty(dog, "DOG", DeclarationType.ENUM_CONST, "没错，就是🐶");
    }

    private void checkProperty(Property property, String name, DeclarationType type, String description) {
        assertThat(property).isNotNull();
        assertThat(property.getDisplayName()).isEqualTo(name);
        assertThat(property.getDeclaration().getType()).isEqualTo(type);
        assertThat(property.getDescription()).isEqualTo(description);
    }

    private void checkTypeVarDeclaration(TypeVarDeclaration property, String name, String description) {
        assertThat(property).isNotNull();
        assertThat(property.getName()).isEqualTo(name);
        assertThat(property.getDescription()).isEqualTo(description);
    }
}
