package com.chendayu.dydoc;

import com.chendayu.dydoc.processor.Warehouse;
import com.chendayu.dydoc.testapp.controller.EmptyController;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmptyResourceTest extends AbstractTest {

    @Test
    public void compileEmpty() {
        Warehouse warehouse = compile(EmptyController.class);
        assertThat(warehouse.getResources()).isEmpty();
    }
}
