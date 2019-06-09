package com.chendayu.c2d.processor;

/**
 * 支持的响应 Content-Type
 */
public enum SupportedContentType {

    APPLICATION_JSON("application/json", false),
    MULTIPART_FORM_DATA("multipart/form-data", true),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded", true);

    private final String value;

    private final boolean parameterInBody;

    SupportedContentType(String value, boolean parameterInBody) {
        this.value = value;
        this.parameterInBody = parameterInBody;
    }

    public static SupportedContentType infer(String[] contentTypes) {
        for (String contentType : contentTypes) {
            if (contentType.contains(APPLICATION_JSON.getValue())) {
                return APPLICATION_JSON;
            }

            if (contentType.contains(MULTIPART_FORM_DATA.getValue())) {
                return MULTIPART_FORM_DATA;
            }

            if (contentType.contains(APPLICATION_FORM_URLENCODED.getValue())) {
                return APPLICATION_FORM_URLENCODED;
            }
        }

        return APPLICATION_JSON;
    }

    public String getValue() {
        return value;
    }

    public boolean isParameterInBody() {
        return parameterInBody;
    }
}
