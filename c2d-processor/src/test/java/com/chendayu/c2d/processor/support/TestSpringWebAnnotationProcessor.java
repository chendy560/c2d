package com.chendayu.c2d.processor.support;

import com.chendayu.c2d.processor.SpringWebAnnotationProcessor;
import com.chendayu.c2d.processor.Warehouse;

/**
 * 测试用 processor，把 warehouse 暴露出来
 */
public class TestSpringWebAnnotationProcessor extends SpringWebAnnotationProcessor {

    @Override
    public Warehouse getWarehouse() {
        return super.getWarehouse();
    }
}
