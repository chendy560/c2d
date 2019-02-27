package com.chendayu.dydoc.processor.declaration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DeclarationTest {
}
