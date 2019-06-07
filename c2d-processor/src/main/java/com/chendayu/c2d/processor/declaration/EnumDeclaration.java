package com.chendayu.c2d.processor.declaration;

import java.util.List;

import com.chendayu.c2d.processor.property.Property;
import com.chendayu.c2d.processor.util.NameConversions;

/**
 * 枚举类型，API返回中枚举可以作为对象，但是这里只考虑了作为字符串之类的返回的情况
 * 以及，因为获取到构造方法参数需要深入到一些标准外的特殊api，暂时懒着挖了
 */
public class EnumDeclaration implements Declaration {

    private final String qualifiedName;

    private final String name;

    private final String link;

    private final List<Property> constants;

    private final List<String> description;

    public EnumDeclaration(String name, String qualifiedName,
                           List<Property> constants, List<String> description) {
        this.name = name;
        this.link = NameConversions.componentEnumLink(qualifiedName);
        this.qualifiedName = qualifiedName;
        this.constants = constants;
        this.description = description;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public List<Property> getConstants() {
        return constants;
    }

    public List<String> getDescription() {
        return description;
    }

    @Override
    public DeclarationType getType() {
        return DeclarationType.ENUM;
    }
}
