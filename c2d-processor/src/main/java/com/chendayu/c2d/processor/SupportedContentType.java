package com.chendayu.c2d.processor;

import java.util.List;

/**
 * 支持的响应 Content-Type
 */
public enum SupportedContentType {

    APPLICATION_JSON("application/json"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded");

    private final String value;

    SupportedContentType(String value) {
        this.value = value;
    }

    public static SupportedContentType infer(List<String> contentTypes) {
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
}
