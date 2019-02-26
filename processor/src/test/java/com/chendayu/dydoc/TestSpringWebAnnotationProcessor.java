package com.chendayu.dydoc;

import com.chendayu.dydoc.processor.SpringWebAnnotationProcessor;
import com.chendayu.dydoc.processor.Warehouse;

public class TestSpringWebAnnotationProcessor extends SpringWebAnnotationProcessor {

    @Override
    public Warehouse getWarehouse() {
        return super.getWarehouse();
    }
}
