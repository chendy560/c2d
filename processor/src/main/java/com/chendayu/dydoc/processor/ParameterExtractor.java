package com.chendayu.dydoc.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.List;

public class ParameterExtractor extends InfoExtractor {

    private ParameterTypeHelper typeHelper;

    public ParameterExtractor(ProcessingEnvironment processEnv, ApiInfoStore store) {
        super(processEnv, store);
        this.typeHelper = new ParameterTypeHelper(processEnv);
    }

    public Parameter getParameter(String name, List<String> description, VariableElement element) {
        return getParameter(name, description, element.asType());
    }

    public Parameter getParameter(String name, List<String> description, TypeMirror typeMirror) {
        Parameter parameter = new Parameter(name);
        parameter.setDescription(description);

        ParameterType parameterType = typeHelper.findType(typeMirror);
        parameter.setType(parameterType);

        if (parameterType == ParameterType.OBJECT || parameterType == ParameterType.ENUM) {
            ObjectStruct objectStruct = getAndSaveObject(typeMirror);
            parameter.setObjectName(objectStruct.getName());
            parameter.setObjectHash(objectStruct.getHash());
            return parameter;
        }

        if (parameterType == ParameterType.ARRAY) {
            ObjectStruct objectStruct = getAndSaveObjectGeneric(typeMirror);
            parameter.setObjectName(objectStruct.getName());
            parameter.setObjectHash(objectStruct.getHash());
            return parameter;
        }
        return parameter;
    }

    private ObjectStruct getAndSaveObjectGeneric(TypeMirror typeMirror) {
        DeclaredType declaredType = (DeclaredType) typeMirror;
        return getAndSaveObject(declaredType.getTypeArguments().get(0));
    }

    private ObjectStruct getAndSaveObject(TypeMirror typeMirror) {
        // MUST DECLARED
        Element typeMirrorElement = typesUtils.asElement(typeMirror);
        TypeElement typeElement = null;
        try {
            typeElement = (TypeElement) typeMirrorElement;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
        String name = typeElement.getSimpleName().toString();
        ObjectStruct savedObject = store.getObject(name);
        if (savedObject != null) {
            return savedObject;
        }
        ObjectStruct objectStruct = new ObjectStruct(name);
        List<String> description = DocComment.create(elementUtils.getDocComment(typeElement)).getDescription();
        objectStruct.setDescription(description);
        store.addObject(objectStruct);

        List<? extends Element> allMembers = elementUtils.getAllMembers(typeElement);

        List<VariableElement> fields = ElementFilter.fieldsIn(allMembers);

        if (typeElement.getKind() == ElementKind.ENUM) {
            for (Element field : fields) {
                if (field.getKind() == ElementKind.ENUM_CONSTANT) {
                    String fieldName = field.getSimpleName().toString();
                    List<String> fieldDescription = DocComment.create(elementUtils.getDocComment(field))
                            .getDescription();
                    Parameter parameter = new Parameter(fieldName);
                    parameter.setType(ParameterType.ENUM_CONST);
                    parameter.setDescription(fieldDescription);
                    objectStruct.addParameter(parameter);
                }
            }
            return objectStruct;
        }

        for (VariableElement variableElement : fields) {
            String filedName = variableElement.getSimpleName().toString();
            List<String> fieldDescription = DocComment.create(elementUtils.getDocComment(variableElement))
                    .getDescription();
            Parameter parameter = getParameter(filedName, fieldDescription, variableElement);
            objectStruct.addParameter(parameter);
        }

        return objectStruct;
    }
}
