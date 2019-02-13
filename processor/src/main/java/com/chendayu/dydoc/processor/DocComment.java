package com.chendayu.dydoc.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocComment {

    private static final Pattern SPLIT = Pattern.compile("\n");

    private static final Pattern PARAM_PATTERN = Pattern.compile(" ?@param +(.*?) +(.*?)");

    private static final Pattern RETURN_PATTERN = Pattern.compile(" ?@return +(.*?)");

    private static final DocComment EMPTY = new DocComment() {
        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public String getParam(String name) {
            return "";
        }

        @Override
        public String getReturn() {
            return "";
        }
    };

    private String description;

    private Map<String, String> param;

    private String returnValue;

    private DocComment() {

    }

    public static DocComment create(String comment) {
        if (comment == null) {
            return EMPTY;
        }

        DocComment docComment = new DocComment();
        String[] commentLines = SPLIT.split(comment);
        StringBuilder builder = new StringBuilder();

        int i = 0;
        int length = commentLines.length;

        String line;
        while (i < length) {
            line = commentLines[i].trim();

            if (line.startsWith("@")) {
                break;
            }

            builder.append(line).append(' ');

            i += 1;
        }

        docComment.description = builder.toString().trim();

        HashMap<String, String> params = new HashMap<>();

        while (i < length) {
            line = commentLines[i].trim();
            i += 1;

            Matcher paramMatcher = PARAM_PATTERN.matcher(line);
            if (paramMatcher.matches()) {
                String name = paramMatcher.group(1);
                String desc = paramMatcher.group(2);
                params.put(name, desc);
                continue;
            }

            Matcher returnMatcher = RETURN_PATTERN.matcher(line);
            if (returnMatcher.matches()) {
                docComment.returnValue = returnMatcher.group(1);
            }
        }
        docComment.param = params;
        return docComment;
    }

    public String getDescription() {

        if (description != null) {
            return description;
        }
        return "";
    }

    public String getParam(String name) {

        String comment = param.get(name);
        if (comment != null) {
            return comment;
        }
        return "";
    }

    public String getReturn() {

        if (returnValue != null) {
            return returnValue;
        }
        return "";
    }

    @Override
    public String toString() {
        return "DocComment{" +
                "description='" + description + '\'' +
                ", param=" + param +
                ", returnValue='" + returnValue + '\'' +
                '}';
    }
}
