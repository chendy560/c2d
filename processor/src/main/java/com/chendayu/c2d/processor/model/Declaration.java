package com.chendayu.c2d.processor.model;

/**
 * 类似于 class 的东西，叫这个名字一定程度上是为了避免和其他 API 的叫法发生冲突
 */
public interface Declaration {

    DeclarationType getType();
}
