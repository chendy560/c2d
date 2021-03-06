package com.chendayu.c2d.processor.action;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.EnumSet;

import com.chendayu.c2d.processor.AbstractComponent;
import com.chendayu.c2d.processor.declaration.ArrayDeclaration;
import com.chendayu.c2d.processor.declaration.Declaration;
import com.chendayu.c2d.processor.declaration.DeclarationExtractor;
import com.chendayu.c2d.processor.declaration.DeclarationType;

/**
 * 抽象实现，准备一些需要用到的字段和方法
 */
public abstract class AbstractParameterHandler extends AbstractComponent implements ParameterHandler {

    private static final EnumSet<DeclarationType> simpleTypes = EnumSet.of(
            DeclarationType.STRING,
            DeclarationType.NUMBER,
            DeclarationType.TIMESTAMP,
            DeclarationType.BOOLEAN,
            DeclarationType.ENUM_CONST,
            DeclarationType.ENUM,
            DeclarationType.FILE
    );

    protected final DeclarationExtractor declarationExtractor;

    public AbstractParameterHandler(ProcessingEnvironment processingEnv, DeclarationExtractor declarationExtractor) {
        super(processingEnv);
        this.declarationExtractor = declarationExtractor;
    }

    /**
     * @return 是否是一个"简单"类型
     */
    protected boolean isSimpleDeclaration(Declaration declaration) {
        DeclarationType type = declaration.getType();
        return simpleTypes.contains(type);
    }

    /**
     * @return 是否是一个"简单"数组
     */
    protected boolean isSimpleArray(Declaration declaration) {
        if (declaration.getType() == DeclarationType.ARRAY) {
            ArrayDeclaration arrayDeclaration = (ArrayDeclaration) declaration;
            Declaration componentType = arrayDeclaration.getItemType();
            return isSimpleDeclaration(componentType);
        }
        return false;
    }
}
