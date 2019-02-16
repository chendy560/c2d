package com.chendayu.dydoc.processor;

public class AdocGenerator {

    private static final String INCLUDE_BEGIN = "include::";
    private static final String INCLUDE_END = "[]";

    private static final String HARDBREAKS = "[%hardbreaks]";
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

    private final StringBuilder builder = new StringBuilder(4096);

    public AdocGenerator() {
        safeLine();
    }

    public String getAndReset() {
        String result = builder.toString();
        builder.setLength(0);
        safeLine();
        return result;
    }

    public AdocGenerator newLine(String s) {
        return append(s).append(NEW_LINE);
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

    public AdocGenerator shrink(int n) {
        builder.setLength(builder.length() - n);
        return this;
    }

    public AdocGenerator append(String s) {
        builder.append(s);
        return this;
    }

    public AdocGenerator append(char s) {
        builder.append(s);
        return this;
    }

    public AdocGenerator beginHardBreaks() {
        return append(HARDBREAKS);
    }

    private void safeLine() {
        newLine().append("// new file begin").newLine();
    }

    public AdocGenerator title0(String title) {
        return append("= ").append(title);
    }

    public AdocGenerator title1(String title) {
        return append("== ").append(title);
    }

    public AdocGenerator title2(String title) {
        return append("=== ").append(title);
    }

    public AdocGenerator title3(String title) {
        return append("==== ").append(title);
    }

    public AdocGenerator title4(String title) {
        return append("===== ").append(title);
    }

    public AdocGenerator title5(String title) {
        return append("====== ").append(title);
    }

    public AdocGenerator include(String file) {
        return append(INCLUDE_BEGIN).append(file).append(INCLUDE_END);
    }

    public AdocGenerator sourceCode(String type) {
        return append(CODE_BEGIN).append(type).append(CODE_END);
    }

    public AdocGenerator codeBoundary() {
        return append(CODE_BOUNDARY);
    }

    public AdocGenerator space() {
        return append(' ');
    }

    public AdocGenerator hardNewLine() {
        return append(HARD_NEW_LINE);
    }
}
