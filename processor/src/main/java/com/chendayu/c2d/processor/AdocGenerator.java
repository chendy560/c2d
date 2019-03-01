package com.chendayu.c2d.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class AdocGenerator {

    private static final String INCLUDE_BEGIN = "include::";
    private static final String INCLUDE_END = "[]";

    private static final String HARD_NEW_LINE = " +\n";

    private static final String LINK_END = ">>";
    private static final String LINK_BEGIN = "<<";
    private static final String LINK_SEPARATOR = ",";
    private static final char NEW_LINE = '\n';
    private static final String ANCHOR_BEGIN = "[[";
    private static final String ANCHOR_END = "]]";
    private static final String TABLE_BOUNDARY = "|===";
    private static final char TABLE_SEPARATOR = '|';
    private static final String CODE_BEGIN = "[source,";
    private static final char CODE_END = ']';
    private static final String CODE_BOUNDARY = "----";

    private final Writer writer;

    public AdocGenerator(Writer writer) {
        this.writer = writer;
        safeLine();
    }

    public AdocGenerator tableBoundary() {
        return append(TABLE_BOUNDARY);
    }

    public AdocGenerator tableSeparator() {
        return append(TABLE_SEPARATOR);
    }

    public AdocGenerator newLine() {
        return append(NEW_LINE);
    }

    public AdocGenerator dualNewLine() {
        return newLine().newLine();
    }

    public AdocGenerator anchor(String anchor) {
        return append(ANCHOR_BEGIN).append(anchor).append(ANCHOR_END);
    }

    public AdocGenerator link(String anchor, String name) {
        return append(LINK_BEGIN)
                .append(anchor).append(LINK_SEPARATOR).append(name)
                .append(LINK_END);
    }

    public AdocGenerator append(String s) {
        try {
            writer.append(s);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public AdocGenerator append(char s) {
        try {
            writer.append(s);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    private void safeLine() {
        newLine().append("// new file begin").newLine();
    }

    private AdocGenerator title(String t, String title) {
        return append(t).append(title).dualNewLine();
    }

    public AdocGenerator title0(String title) {
        return title("= ", title);
    }

    public AdocGenerator title1(String title) {
        return title("= ", title);
    }

    public AdocGenerator title2(String title) {
        return title("== ", title);
    }

    public AdocGenerator title3(String title) {
        return title("=== ", title);
    }

    public AdocGenerator title4(String title) {
        return title("==== ", title);
    }

    public AdocGenerator title5(String title) {
        return title("===== ", title);
    }

    public AdocGenerator include(String file) {
        return append(INCLUDE_BEGIN).append(file).append(INCLUDE_END);
    }

    public AdocGenerator sourceCode(String type) {
        return append(CODE_BEGIN).append(type).append(CODE_END).newLine();
    }

    public AdocGenerator codeBoundary() {
        return append(CODE_BOUNDARY).dualNewLine();
    }

    public AdocGenerator space() {
        return append(' ');
    }

    public AdocGenerator hardNewLine() {
        return append(HARD_NEW_LINE);
    }

    public AdocGenerator appendLines(List<String> ss) {
        if (ss == null) {
            return this;
        }
        if (ss.isEmpty()) {
            return newLine();
        }

        for (String s : ss) {
            if (!s.isEmpty()) {
                append(s).hardNewLine();
            }
        }

        return dualNewLine();
    }
}
