package com.krishimitra.advisory.dto.response;

import com.krishimitra.advisory.enums.AdvisoryCategory;
import com.krishimitra.advisory.enums.AdvisoryLanguage;
import com.krishimitra.advisory.enums.AdvisorySource;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record AdvisoryResponse(
        String id,
        String state,
        String district,
        AdvisorySource source,
        String sourceName,
        String sourceReference,
        String sourceUrl,
        String title,
        String summary,
        String content,
        AdvisoryCategory category,
        AdvisoryLanguage language,
        List<String> cropCodes,
        LocalDate publishedAt,
        LocalDate validFrom,
        LocalDate validUntil,
        boolean verified,
        Instant updatedAt
) {}