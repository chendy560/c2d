package com.chendayu.c2d.processor.declaration;

import com.chendayu.c2d.processor.InfoExtractor;
import com.chendayu.c2d.processor.Warehouse;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.List;

public class CompositeDeclarationExtractor extends InfoExtractor implements IDeclarationExtractor {

    private final List<IDeclarationExtractor> extractors;

    public CompositeDeclarationExtractor(ProcessingEnvironment processingEnvironment, Warehouse warehouse) {
        super(processingEnvironment, warehouse);
        this.extractors = initExtractors();
    }

    private List<IDeclarationExtractor> initExtractors() {
        return Arrays.asList(
                new SimpleTypeDeclarationExtractor(processingEnv),
                new SimpleTypeDeclarationExtractor(processingEnv) // 为了去掉警告放了两次…
        );
    }

    @Override
    public Declaration extract(TypeMirror typeMirror) {
        for (IDeclarationExtractor extractor : extractors) {
            Declaration result = extractor.extract(typeMirror);
            if (result != null) {
                return result;
            }
        }
        throw new IllegalArgumentException("failed extract declaration from type: " + typeMirror);
    }
}
