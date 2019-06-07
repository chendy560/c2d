package com.chendayu.c2d.processor.output;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * 输出 adoc 的工具类
 */
public class AdocWriter {

    private static final String COL_BEGIN = "[cols=\"";
    private static final String COL_END = "\"]";

    private static final char BOLD = '*';
    private static final char MONOSPACE = '`';

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

    private static final String TITLE0 = "=";
    private static final String TITLE1 = "==";
    private static final String TITLE2 = "===";
    private static final String TITLE3 = "====";
    private static final String TITLE4 = "=====";

    private final Writer writer;

    public AdocWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * 打印表格的列控制行
     */
    public void col(String col) {
        append(COL_BEGIN);
        append(col);
        append(COL_END);
        newLine();
    }

    /**
     * 表格开始
     */
    public void tableBoundary() {
        append(TABLE_BOUNDARY);
        dualNewLine();
    }

    /**
     * 一列开始
     */
    public void columnBegin() {
        append(TABLE_SEPARATOR);
    }

    /**
     * 一个换行符
     */
    public void newLine() {
        append(NEW_LINE);
    }

    /**
     * 两个换行符
     */
    public void dualNewLine() {
        newLine();
        newLine();
    }

    /**
     * 下一个锚
     */
    public void anchor(String anchor) {
        append(ANCHOR_BEGIN);
        append(anchor);
        append(ANCHOR_END);
        newLine();
    }

    /**
     * 链接
     */
    public void link(String anchor, String name) {
        append(LINK_BEGIN);
        append(anchor);
        append(LINK_SEPARATOR);
        append(name);
        append(LINK_END);
    }

    /**
     * 单纯地 append 一个字符串
     */
    public void append(String s) {
        try {
            writer.append(s);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 单纯地 append 一个字符
     */
    public void append(char s) {
        try {
            writer.append(s);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 插入一个没有意义的注释行，避免文件 include 时的一些意外的格式错误
     */
    private void safeLine() {
        append("// new file begin");
        newLine();
    }

    /**
     * 标题
     */
    private void title(String t, String title) {
        append(t);
        appendSpace();
        append(title);
        dualNewLine();
    }

    /**
     * 大标题
     */
    public void title0(String title) {
        title(TITLE0, title);
    }

    /**
     * 一级标题
     */
    public void title1(String title) {
        title(TITLE1, title);
    }

    /**
     * 二级标题
     */
    public void title2(String title) {
        title(TITLE2, title);
    }

    /**
     * 三级标题
     */
    public void title3(String title) {
        title(TITLE3, title);
    }

    /**
     * 四级标题
     */
    public void title4(String title) {
        title(TITLE4, title);
    }

    /**
     * 源代码块开始
     */
    public void sourceCodeBegin(String type) {
        append(CODE_BEGIN);
        append(type);
        append(CODE_END);
        newLine();
        append(CODE_BOUNDARY);
        newLine();
    }

    /**
     * 源代码块结束
     */
    public void sourceCodeEnd() {
        append(CODE_BOUNDARY);
        dualNewLine();
    }

    /**
     * 单纯 append 一个空格
     */
    public void appendSpace() {
        append(' ');
    }

    /**
     * 强行换行
     */
    public void hardNewLine() {
        append(HARD_NEW_LINE);
    }

    /**
     * append 一段粗体
     */
    public void appendBoldMonospace(String s) {
        append(MONOSPACE);
        append(BOLD);
        append(s);
        append(BOLD);
        append(MONOSPACE);
    }

    /**
     * append 多行
     */
    public void appendLines(List<String> ss) {

        for (String s : ss) {
            if (!s.isEmpty()) {
                append(s);
                hardNewLine();
            }
        }

        newLine();
    }
}
