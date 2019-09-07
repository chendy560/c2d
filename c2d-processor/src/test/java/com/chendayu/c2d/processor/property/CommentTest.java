package com.chendayu.c2d.processor.property;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {


    @Test
    void simpleTest() {
        Comment comment = new Comment("Test Something here\n" +
                "This is a new line\n" +
                "<p>\n" +
                "This is another new line\n" +
                " \n" +
                "@param p1  parameter1\n" +
                "@param p2  parameter2\n" +
                "           here is another line of parameter2\n" +
                "@param p3  parameter3 xxx\n" +
                "@param <T> type of something\n" +
                "@para <T> type of something\n" +
                "@c2d.example example value here\n" +
                "@c2d.ignore 52518\n" +
                "@return return something");

        String commentText = comment.getCommentText();
        assertThat(commentText).isEqualTo("Test Something here\n" +
                "This is a new line\n" +
                "<p>\n" +
                "This is another new line");

        String example = comment.getExample();
        assertThat(example).isEqualTo("example value here");

        assertThat(comment.isIgnored()).isTrue();

        String returnText = comment.getReturnText();
        assertThat(returnText).isEqualTo("return something");

        String p1 = comment.getParamComment("p1");
        assertThat(p1).isEqualTo("parameter1");

        String p2 = comment.getParamComment("p2");
        assertThat(p2).isEqualTo("parameter2\n" +
                "           here is another line of parameter2");

        String p3 = comment.getParamComment("p3");
        assertThat(p3).isEqualTo("parameter3 xxx");

        String t = comment.getTypeParamComment("T");
        assertThat(t).isEqualTo("type of something");
    }
}
