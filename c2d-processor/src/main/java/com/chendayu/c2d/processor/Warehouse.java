package com.chendayu.c2d.processor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.chendayu.c2d.processor.action.Resource;
import com.chendayu.c2d.processor.declaration.EnumDeclaration;
import com.chendayu.c2d.processor.declaration.NestedDeclaration;

/**
 * 保存解析到的各种信息的仓库
 */
public class Warehouse {

    /**
     * 解析得到的文档会存放到这个包下
     */
    private static final String PACKAGE_NAME = "c2d";

    /**
     * 保存处理过的 {@link NestedDeclaration}
     */
    private final Map<String, NestedDeclaration> nameDeclarationMap = new HashMap<>(16, 0.5f);

    /**
     * 保存处理过的 {@link EnumDeclaration}
     */
    private final Map<String, EnumDeclaration> nameEnumMap = new HashMap<>(16, 0.5f);

    /**
     * 保存提取到的 {@link Resource}
     */
    private final SortedMap<String, Resource> resourceMap = new TreeMap<>();

    /**
     * 默认情况下，spring-boot 的应用主类的 'Application' 结尾
     */
    private String applicationName = "Application";

    /**
     * 默认的"默认包"
     */
    private String basePackage = PACKAGE_NAME;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage + '.' + PACKAGE_NAME;
    }

    public NestedDeclaration getDeclaration(String qualifiedName) {
        return nameDeclarationMap.get(qualifiedName);
    }

    public void addDeclaration(NestedDeclaration declaration) {
        nameDeclarationMap.put(declaration.getQualifiedName(), declaration);
    }

    public boolean containsResource(String name) {
        return resourceMap.containsKey(name);
    }

    public void addResource(Resource resource) {
        resourceMap.put(resource.getName(), resource);
    }

    public Collection<Resource> getResources() {
        return resourceMap.values();
    }

    public EnumDeclaration getEnumDeclaration(String qualifiedName) {
        return nameEnumMap.get(qualifiedName);
    }

    public void addEnumDeclaration(EnumDeclaration enumDeclaration) {
        nameEnumMap.put(enumDeclaration.getQualifiedName(), enumDeclaration);
    }
}
