package com.chendayu.dydoc.processor.support;

import com.chendayu.dydoc.processor.SpringWebAnnotationProcessor;
import com.chendayu.dydoc.processor.Warehouse;

/**
 * 测试用 processor，把 warehouse 暴露出来
 */
public class TestSpringWebAnnotationProcessor extends SpringWebAnnotationProcessor {

    @Override
    public Warehouse getWarehouse() {
        return super.getWarehouse();
    }
}
