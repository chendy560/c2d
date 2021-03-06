package com.chendayu.c2d.processor.declaration;

import java.util.List;

import com.chendayu.c2d.processor.property.Property;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumTest extends AbstractDeclarationTest {

    @Test
    public void testCollection() {
        List<Declaration> declarations = compile(EnumTestSupport.class);

        assertThat(declarations).hasSize(1);

        EnumDeclaration enumDeclaration = (EnumDeclaration) declarations.get(0);


        assertThat(enumDeclaration.getName()).isEqualTo("UserType");
        assertThat(enumDeclaration.getDescription()).isEqualTo("用户类型");

        List<Property> constants = enumDeclaration.getConstants();
        assertThat(constants).hasSize(2);

        Property normal = constants.get(0);
        checkProperty(normal, "NORMAL", DeclarationType.ENUM_CONST, "普通类型，级别1");

        Property admin = constants.get(1);
        checkProperty(admin, "ADMIN", DeclarationType.ENUM_CONST, "管理员，级别2");

    }
}
