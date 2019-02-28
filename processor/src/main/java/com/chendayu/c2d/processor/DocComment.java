package com.chendayu.c2d.processor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocComment {

    private static final Pattern SPLIT = Pattern.compile("\n");

    /**
     * 匹配 " @param name desc "
     */
    private static final Pattern PARAM_PATTERN = Pattern.compile(" ?@param +(.*?) +(.*?)?");

    /**
     * 匹配 " @return blah"
     */
    private static final Pattern RETURN_PATTERN = Pattern.compile(" ?@return +(.*?)?");

    /**
     * 注释本身在map中的key，使用 this 避免和参数名字重复
     */
    private static final String DESCRIPTION_KEY = "@this";

    /**
     * "@return" 注释，使用 return 避免和参数重复
     */
    private static final String RETURN_KEY = "@return";

    /**
     * 没有注释时返回一个空对象
     */
    private static final DocComment EMPTY = new DocComment() {

        @Override
        public List<String> getDescription() {
            return Collections.emptyList();
        }

        @Override
        public List<String> getParam(String name) {
            return Collections.emptyList();
        }

        @Override
        public List<String> getReturn() {
            return Collections.emptyList();
        }

        @Override
        public List<String> getTypeParam(String name) {
            return Collections.emptyList();
        }
    };

    private Map<String, List<String>> comments;

    private DocComment() {
    }

    static DocComment create(String comment) {
        if (comment == null) {
            return EMPTY;
        }

        DocComment docComment = new DocComment();
        String[] commentLines = SPLIT.split(comment);

        HashMap<String, List<String>> commentsMap = new HashMap<>();

        boolean skipEmptyLine = true;

        ArrayList<String> descriptionLines = new ArrayList<>();
        String currentParameterName = DESCRIPTION_KEY;

        commentsMap.put(currentParameterName, descriptionLines);

        for (String l : commentLines) {

            String line = l.trim();

            if (line.isEmpty() && skipEmptyLine) {
                continue;
            }
            skipEmptyLine = false;

            Matcher paramMatcher = PARAM_PATTERN.matcher(line);
            if (paramMatcher.matches()) {
                trim(commentsMap.get(currentParameterName));

                currentParameterName = paramMatcher.group(1);
                ArrayList<String> paramLines = new ArrayList<>();
                if (paramMatcher.groupCount() == 2) {
                    paramLines.add(paramMatcher.group(2));
                }
                commentsMap.put(currentParameterName, paramLines);
                skipEmptyLine = true;
                continue;
            }

            Matcher returnMatcher = RETURN_PATTERN.matcher(line);
            if (returnMatcher.matches()) {
                trim(commentsMap.get(currentParameterName));

                currentParameterName = RETURN_KEY;
                ArrayList<String> returnLines = new ArrayList<>();
                if (returnMatcher.groupCount() == 1) {
                    returnLines.add(returnMatcher.group(1));
                }
                commentsMap.put(currentParameterName, returnLines);
                skipEmptyLine = true;
                continue;
            }

            if (line.startsWith("@")) {
                continue; // todo 这里要考虑改一下以适应所有的情况
            }
            commentsMap.get(currentParameterName).add(line);
        }

        docComment.comments = commentsMap;
        return docComment;
    }

    private static void trim(List<String> s) {
        for (int i = s.size() - 1; i >= 0; i--) {
            if (s.get(i).isEmpty()) {
                s.remove(i);
            } else {
                break;
            }
        }

        for (int i = 0; i < s.size(); i++) {
            if (s.get(i).isEmpty()) {
                s.remove(i);
            } else {
                break;
            }
        }
    }

    public List<String> getParam(String name) {

        List<String> comment = comments.get(name);
        if (comment != null) {
            return comment;
        }
        return Collections.emptyList();
    }

    public List<String> getTypeParam(String name) {

        String typeParamName = "<" + name + '>';
        List<String> comment = comments.get(typeParamName);
        if (comment != null) {
            return comment;
        }
        return Collections.emptyList();
    }

    public List<String> getDescription() {
        return getParam(DESCRIPTION_KEY);
    }

    public List<String> getReturn() {
        return getParam(RETURN_KEY);
    }
}
