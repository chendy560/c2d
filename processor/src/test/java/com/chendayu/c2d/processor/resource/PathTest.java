package com.chendayu.c2d.processor.resource;

import com.chendayu.c2d.processor.Warehouse;
import com.chendayu.c2d.processor.model.Resource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PathTest extends AbstractResourceTest {

    @Test
    public void test() {
        Warehouse warehouse = compile(PathTestController.class);
        Resource resource = warehouse.getResources().iterator().next();
        assertThat(resource.getPath()).isEqualTo("/base/test");
    }
}
