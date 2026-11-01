package com.krishimitra.common.response;

import java.util.Map;

public record ErrorResponse(
        String error,
        Map<String, String> fieldErrors
) {
}