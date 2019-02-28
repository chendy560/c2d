package com.chendayu.c2d.processor.declaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@DeclarationTest
public class CollectionTestSupport {

    @DeclarationTest
    public void test(Collection<String> cs,
                     List<Integer> li,
                     Set<Boolean> sb,
                     UnsupportedCollection uc) {
    }

    public static class UnsupportedCollection extends ArrayList<String> {

    }
}
