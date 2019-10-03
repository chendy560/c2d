package com.chendayu.c2d.processor.property;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import java.util.HashMap;
import java.util.Map;

/**
 * 直接参考（抄袭）了 javadoc 对注释的解析逻辑
 *
 * @see com.sun.tools.javadoc.Comment
 */
public class Comment {


    private static final String PARAM_TAG = "@param";
    private static final String RETURN_TAG = "@return";

    private static final String C2D_EXAMPLE = "@c2d.example";
    private static final String C2D_IGNORE = "@c2d.ignore";

    private static final char TAG_BEGIN = '@';
    private static final char TYPE_PARAM_BEGIN = '<';
    private static final char TYPE_PARAM_END = '>';

    private static final int IN_TEXT = 1;
    private static final int TAG_GAP = 2;
    private static final int TAG_NAME = 3;

    private static final Comment empty = new Comment(null) {

        @Override
        public String getCommentText() {
            return "";
        }

        @Override
        public String getReturnText() {
            return "";
        }

        @Override
        public String getExample() {
            return "";
        }

        @Override
        public boolean isIgnored() {
            return false;
        }

        @Override
        public String getParamComment(String paramName) {
            return "";
        }

        @Override
        public String getTypeParamComment(String paramName) {
            return "";
        }
    };

    private static Elements elementUtils;

    private final String commentString;

    private String commentText = "";

    private String returnText = "";

    private Map<String, String> params = new HashMap<>();

    private Map<String, String> typeParameters = new HashMap<>();

    private String example;

    private boolean ignored;

    public Comment(String commentString) {
        this.commentString = commentString == null ? "" : commentString;
        init();
    }

    public static void initStatic(ProcessingEnvironment processingEnv) {
        elementUtils = processingEnv.getElementUtils();
    }

    public static Comment create(Element comment) {
        if (comment == null) {
            return empty;
        }
        String docComment = elementUtils.getDocComment(comment);
        return new Comment(docComment);
    }

    private void init() {  // NOSONAR 对不起，这是我从JDK抄过来的，复杂度爆了就爆了吧
        int state = TAG_GAP;
        boolean newLine = true;
        String tagName = null;
        int tagStart = 0;
        int textStart = 0;
        int lastNonWhite = -1;
        int len = commentString.length();
        for (int inx = 0; inx < len; ++inx) {
            char ch = commentString.charAt(inx);
            boolean isWhite = Character.isWhitespace(ch);
            switch (state) {
                case TAG_NAME:
                    if (isWhite) {
                        tagName = commentString.substring(tagStart, inx);
                        state = TAG_GAP;
                    }
                    break;
                case TAG_GAP:
                    if (isWhite) {
                        break;
                    }
                    textStart = inx;
                    state = IN_TEXT;
                    /* fall thru */
                case IN_TEXT:
                    if (newLine && ch == TAG_BEGIN) {
                        parseCommentComponent(tagName, textStart,
                                lastNonWhite + 1);
                        tagStart = inx;
                        state = TAG_NAME;
                    }
                    break;
                default:
                    break;
            }
            if (ch == '\n') {
                newLine = true;
            } else if (!isWhite) {
                lastNonWhite = inx;
                newLine = false;
            }
        }
        // Finish what's currently being processed
        switch (state) {
            case TAG_NAME:
                tagName = commentString.substring(tagStart, len);
                /* fall thru */
            case TAG_GAP:
                textStart = len;
                /* fall thru */
            case IN_TEXT:
                parseCommentComponent(tagName, textStart, lastNonWhite + 1);
                break;
            default:
                break;
        }
    }

    private void parseCommentComponent(String tagName,
                                       int from, int to) {
        String text = to <= from ? "" : commentString.substring(from, to);
        if (tagName == null) {
            commentText = text;
        } else {
            switch (tagName) {
                case PARAM_TAG:
                    parseParamTag(text);
                    break;
                case RETURN_TAG:
                    this.returnText = text;
                    break;
                case C2D_IGNORE:
                    this.ignored = true;
                    break;
                case C2D_EXAMPLE:
                    this.example = text;
                    break;
                default:
                    break;
            }
        }
    }

    private void parseParamTag(String text) {
        if (text.charAt(0) == TYPE_PARAM_BEGIN) {
            parseTypeParamTag(text);
        } else {
            parseNormalParamTag(text);
        }
    }

    private void parseNormalParamTag(String text) {
        StringBuilder nameBuilder = new StringBuilder();
        int length = text.length();
        int idx = 0;
        for (; idx < length; idx++) {
            char c = text.charAt(idx);
            if (!Character.isWhitespace(c)) {
                nameBuilder.append(c);
            } else {
                break;
            }
        }

        String paramName = nameBuilder.toString();
        for (; idx < length; idx++) {
            char c = text.charAt(idx);
            if (!Character.isWhitespace(c)) {
                String paramComment = text.substring(idx);
                params.put(paramName, paramComment);
                break;
            }
        }
    }

    private void parseTypeParamTag(String text) {
        StringBuilder nameBuilder = new StringBuilder();
        int length = text.length();
        int idx = 1;
        for (; idx < length; idx++) {
            char c = text.charAt(idx);
            if (c == TYPE_PARAM_END) {
                idx += 1;
                break;
            }

            if (Character.isWhitespace(c)) {
                return;
            }

            nameBuilder.append(c);
        }

        String paramName = nameBuilder.toString();
        for (; idx < length; idx++) {
            char c = text.charAt(idx);
            if (!Character.isWhitespace(c)) {
                String paramComment = text.substring(idx);
                typeParameters.put(paramName, paramComment);
                break;
            }
        }
    }

    public String getCommentText() {
        return commentText;
    }

    public String getReturnText() {
        return returnText;
    }

    public String getExample() {
        return example;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public String getParamComment(String paramName) {
        String comment = params.get(paramName);
        if (comment != null) {
            return comment;
        } else {
            return "";
        }
    }

    public String getTypeParamComment(String paramName) {
        String comment = typeParameters.get(paramName);
        if (comment != null) {
            return comment;
        } else {
            return "";
        }
    }
}
