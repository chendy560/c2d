package com.chendayu.dydoc;

import com.chendayu.dydoc.processor.Resource;
import com.chendayu.dydoc.processor.Warehouse;
import com.chendayu.dydoc.testapp.controller.SimpleController;
import org.junit.Test;

public class SimpleTests extends AbstractTest {

    @Test
    public void compileSimple() {
        Warehouse warehouse = compile(SimpleController.class);

        for (Resource resource : warehouse.getResources()) {
            System.out.println();
        }
    }
}
