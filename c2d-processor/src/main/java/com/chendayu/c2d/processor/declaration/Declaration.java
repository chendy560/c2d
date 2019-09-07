package com.chendayu.c2d.processor.declaration;

/**
 * 类型，叫这个名字一定程度上是为了避免和其他 API 的命名发生冲突
 */
public interface Declaration {

    DeclarationType getType();

    String getDescription();

    boolean equals(Object o);
}
