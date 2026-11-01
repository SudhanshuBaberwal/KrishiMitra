package com.krishimitra.advisory.dto;

import com.krishimitra.advisory.enums.AdvisoryCategory;
import com.krishimitra.advisory.enums.AdvisoryLanguage;

public record AdvisorySearchRequest(
        AdvisoryCategory category,
        AdvisoryLanguage language,
        String cropCode
) {}