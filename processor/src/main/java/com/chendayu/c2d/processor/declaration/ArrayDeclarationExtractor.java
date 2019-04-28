package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.InfoExtractor;
import com.chendayu.c2d.processor.Warehouse;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;

import static com.chendayu.c2d.processor.declaration.Declarations.arrayOf;

/**
 * 抽数组
 */
public class ArrayDeclarationExtractor extends InfoExtractor implements IDeclarationExtractor {

    /**
     * {@link Collection}
     */
    private final TypeMirror collectionType;

    private final CompositeDeclarationExtractor compositeDeclarationExtractor;

    public ArrayDeclarationExtractor(ProcessingEnvironment processingEnvironment, Warehouse warehouse,
                                     CompositeDeclarationExtractor compositeDeclarationExtractor) {
        super(processingEnvironment, warehouse);
        this.collectionType = typeUtils.erasure(elementUtils.getTypeElement(Collection.class.getName()).asType());
        this.compositeDeclarationExtractor = compositeDeclarationExtractor;
    }

    @Override
    public Declaration extract(TypeMirror typeMirror) {
        TypeKind kind = typeMirror.getKind();
        if (kind == TypeKind.ARRAY) {
            return extractFromArrayType((ArrayType) typeMirror);
        }

        if (kind != TypeKind.DECLARED) {
            return null;
        }

        if (typeUtils.isSubtype(typeMirror, collectionType)) {
            return extractFromCollection((DeclaredType) typeMirror);
        }

        return null;
    }

    private Declaration extractFromArrayType(ArrayType typeMirror) {
        ArrayType arrayType = typeMirror;
        TypeMirror componentType = arrayType.getComponentType();
        Declaration componentDeclaration = compositeDeclarationExtractor.extract(componentType);
        return arrayOf(componentDeclaration);
    }

    private Declaration extractFromCollection(DeclaredType declaredType) {
        //todo 万恶的泛型啊
        return null;
    }
}
