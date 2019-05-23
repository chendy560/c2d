package com.chendayu.c2d.processor.output;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * 输出 adoc 的工具类
 */
public class AdocWriter {

    private static final char ITALIC = '_';
    private static final char BOLD = '*';
    private static final char MONOSPACE = '`';

    private static final String INCLUDE_BEGIN = "include::";
    private static final String INCLUDE_END = "[]";

    private static final String COL_BEGIN = "[cols=\"";
    private static final String COL_END = "\"]";

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

    public AdocWriter(Writer writer) {
        this.writer = writer;
        safeLine();
    }

    /**
     * 打印表格的列控制行
     */
    public AdocWriter col(String col) {
        return append(COL_BEGIN).append(col).append(COL_END).newLine();
    }

    /**
     * 表格开始
     */
    public AdocWriter tableBegin() {
        return append(TABLE_BOUNDARY).dualNewLine();
    }

    /**
     * 表格结束
     */
    public AdocWriter tableEnd() {
        return append(TABLE_BOUNDARY).dualNewLine();
    }

    /**
     * 一列开始
     */
    public AdocWriter columnBegin() {
        return append(TABLE_SEPARATOR);
    }

    /**
     * 一个换行符
     */
    public AdocWriter newLine() {
        return append(NEW_LINE);
    }

    /**
     * 两个换行符
     */
    public AdocWriter dualNewLine() {
        return newLine().newLine();
    }

    /**
     * 下一个锚
     */
    public AdocWriter anchor(String anchor) {
        return append(ANCHOR_BEGIN).append(anchor).append(ANCHOR_END).newLine();
    }

    /**
     * 链接
     */
    public AdocWriter link(String anchor, String name) {
        return append(LINK_BEGIN)
                .append(anchor).append(LINK_SEPARATOR).append(name)
                .append(LINK_END);
    }

    /**
     * append 一段粗体
     */
    public AdocWriter appendMonospace(String s) {
        return append(MONOSPACE).append(s).append(MONOSPACE);
    }

    /**
     * append 一段粗体
     */
    public AdocWriter appendBoldMonospace(String s) {
        return append(MONOSPACE).append(BOLD)
                .append(s)
                .append(BOLD).append(MONOSPACE);
    }

    /**
     * append 一段斜体
     */
    public AdocWriter appendItalic(String s) {
        return append(ITALIC).append(s).append(ITALIC);
    }

    /**
     * append 一段斜体
     */
    public AdocWriter appendItalic(char s) {
        return append(ITALIC).append(s).append(ITALIC);
    }

    /**
     * 单纯地 append 一个字符串
     */
    public AdocWriter append(String s) {
        try {
            writer.append(s);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    /**
     * 单纯地 append 一个字符
     */
    public AdocWriter append(char s) {
        try {
            writer.append(s);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    /**
     * 插入一个没有意义的注释行，避免文件 include 时的一些意外的格式错误
     */
    private void safeLine() {
        append("// new file begin").newLine();
    }

    /**
     * 标题
     */
    private AdocWriter title(String t, String title) {
        return append(t).append(title).dualNewLine();
    }

    /**
     * 大标题
     */
    public AdocWriter title0(String title) {
        return title("= ", title);
    }

    /**
     * 一级标题
     */
    public AdocWriter title1(String title) {
        return title("= ", title);
    }

    /**
     * 二级标题
     */
    public AdocWriter title2(String title) {
        return title("== ", title);
    }

    /**
     * 三级标题
     */
    public AdocWriter title3(String title) {
        return title("=== ", title);
    }

    /**
     * 四级标题
     */
    public AdocWriter title4(String title) {
        return title("==== ", title);
    }

    /**
     * 五级标题
     */
    public AdocWriter title5(String title) {
        return title("===== ", title);
    }

    /**
     * 包含其他文件
     */
    public AdocWriter include(String file) {
        return append(INCLUDE_BEGIN).append(file).append(INCLUDE_END);
    }

    /**
     * 源代码块开始
     */
    public AdocWriter sourceCodeBegin(String type) {
        return append(CODE_BEGIN).append(type).append(CODE_END)
                .newLine()
                .append(CODE_BOUNDARY).newLine();
    }

    /**
     * 源代码块结束
     */
    public AdocWriter sourceCodeEnd() {
        return append(CODE_BOUNDARY).dualNewLine();
    }

    /**
     * 单纯 append 一个空格
     */
    public AdocWriter space() {
        return append(' ');
    }

    /**
     * 强行换行
     */
    public AdocWriter hardNewLine() {
        return append(HARD_NEW_LINE);
    }

    /**
     * append 多行
     */
    public AdocWriter appendLines(List<String> ss) {
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

        return newLine();
    }
}
