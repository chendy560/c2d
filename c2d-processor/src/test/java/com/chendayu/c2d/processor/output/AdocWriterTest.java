package com.chendayu.c2d.processor.output;

import java.io.StringWriter;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdocWriterTest {

    private StringWriter stringWriter;
    private AdocWriter adocWriter;

    @BeforeEach
    void init() {
        stringWriter = new StringWriter();
        adocWriter = new AdocWriter(stringWriter);
    }

    @Test
    void col() {
        adocWriter.col("3,7");
        assertResult("[cols=\"3,7\"]\n");
    }

    @Test
    void tableBoundary() {
        adocWriter.tableBoundary();
        assertResult("|===\n\n");
    }

    @Test
    void columnBegin() {
        adocWriter.columnBegin();
        assertResult("|");
    }

    @Test
    void newLine() {
        adocWriter.newLine();
        assertResult("\n");
    }

    @Test
    void dualNewLine() {
        adocWriter.dualNewLine();
        assertResult("\n\n");
    }

    @Test
    void anchor() {
        adocWriter.anchor("hello");
        assertResult("[[hello]]\n");
    }

    @Test
    void link() {
        adocWriter.link("hello", "hi");
        assertResult("<<hello,hi>>");
    }

    @Test
    void appendString() {
        adocWriter.append("hello");
        assertResult("hello");
    }

    @Test
    void appendChar() {
        adocWriter.append('a');
        assertResult("a");
    }

    @Test
    void title0() {
        adocWriter.title0("title");
        assertResult("= title\n\n");
    }

    @Test
    void title1() {
        adocWriter.title1("title");
        assertResult("== title\n\n");
    }

    @Test
    void title2() {
        adocWriter.title2("title");
        assertResult("=== title\n\n");
    }

    @Test
    void title3() {
        adocWriter.title3("title");
        assertResult("==== title\n\n");
    }

    @Test
    void title4() {
        adocWriter.title4("title");
        assertResult("===== title\n\n");
    }

    @Test
    void sourceCodeBegin() {
        adocWriter.sourceCodeBegin("java");
        assertResult("[source,java]\n----\n");
    }

    @Test
    void sourceCodeEnd() {
        adocWriter.sourceCodeEnd();
        assertResult("----\n\n");
    }

    @Test
    void appendSpace() {
        adocWriter.appendSpace();
        assertResult(" ");
    }

    @Test
    void hardNewLine() {
        adocWriter.hardNewLine();
        assertResult(" +\n");
    }

    @Test
    void appendBoldMonospace() {
        adocWriter.appendBoldMonospace("x");
        assertResult("`*x*`");
    }

    @Test
    void appendLines() {
        adocWriter.appendLines(Arrays.asList("line1", "line2", "line3"));
        assertResult("line1 +\nline2 +\nline3 +\n\n");
    }

    private void assertResult(String expect) {
        String result = stringWriter.toString();
        assertThat(result).isEqualTo(expect);
    }
}
