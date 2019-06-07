package com.chendayu.c2d.processor.util;

public class NameConversions {

    private static final String COMPONENT_PREFIX = "components-";
    private static final String COMPONENT_OBJECT_PREFIX = COMPONENT_PREFIX + "objects-";
    private static final String COMPONENT_ENUM_PREFIX = COMPONENT_PREFIX + "enums-";

    private static final String RESOURCE_PREFIX = "resources-";
    private static final String ACTION_PREFIX = "actions-";

    private static final char RESOURCE_ACTION_SEPARATOR = '.';

    private static final char LINK_SEPARATOR = '-';
    private static final char PACKAGE_SEPARATOR = '.';
    private static final char SUB_CLASS_SEPARATOR = '$';

    private NameConversions() {
    }

    public static String componentObjectLink(String qualifiedName) {
        StringBuilder builder = getStringBuilder();
        builder.append(COMPONENT_OBJECT_PREFIX);
        appendQualifiedName(builder, qualifiedName);
        return builder.toString();
    }

    public static String componentEnumLink(String qualifiedName) {
        StringBuilder builder = getStringBuilder();
        builder.append(COMPONENT_ENUM_PREFIX);
        appendQualifiedName(builder, qualifiedName);
        return builder.toString();
    }

    public static String resourceLink(String resourceName) {
        StringBuilder builder = getStringBuilder();
        builder.append(RESOURCE_PREFIX);
        appendQualifiedName(builder, resourceName);
        return builder.toString();
    }

    public static String actionLink(String resourceLink, String actionName) {
        StringBuilder builder = getStringBuilder();
        builder.append(resourceLink);
        builder.append(ACTION_PREFIX);
        appendQualifiedName(builder, actionName);
        return builder.toString();
    }

    public static String actionFullName(String resourceName, String actionName) {
        StringBuilder builder = StringBuilderHolder.resetAndGet();
        return builder.append(resourceName)
                .append(RESOURCE_ACTION_SEPARATOR)
                .append(actionName)
                .toString();
    }

    private static void appendQualifiedName(StringBuilder builder, String qualifiedName) {
        char pc = ' ';
        for (char c : qualifiedName.toCharArray()) {
            if (c == PACKAGE_SEPARATOR || c == SUB_CLASS_SEPARATOR) {
                builder.append(LINK_SEPARATOR);
            } else if (Character.isUpperCase(c) && pc != PACKAGE_SEPARATOR) {
                builder.append(LINK_SEPARATOR).append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
            pc = c;
        }
    }

    /**
     * 方法名 -> {@link com.chendayu.c2d.processor.action.Action} 名字
     * 就是把首字母大写
     *
     * @param s 方法名
     * @return Action 名
     */
    public static String methodNameToActionName(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            char[] chars = s.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }

        return s;
    }

    /**
     * 将一个（通常是 getter 去掉 get 之后的） 字符串转换成 {@link com.chendayu.c2d.processor.property.Property} 名字
     * 就是把首字母小写
     *
     * @param s 某字符串
     * @return Property 名
     */
    public static String toPropertyName(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            char[] chars = s.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }

        return s;
    }

    private static StringBuilder getStringBuilder() {
        return StringBuilderHolder.resetAndGet();
    }
}
