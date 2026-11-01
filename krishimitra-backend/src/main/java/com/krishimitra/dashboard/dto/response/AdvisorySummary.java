package com.krishimitra.dashboard.dto.response;

import com.krishimitra.advisory.enums.AdvisoryCategory;
import com.krishimitra.advisory.enums.AdvisoryLanguage;
import com.krishimitra.advisory.enums.AdvisorySource;

import java.time.LocalDate;
import java.util.List;

public record AdvisorySummary(
        String id,
        String title,
        String summary,
        AdvisorySource source,
        String sourceName,
        String sourceUrl,
        AdvisoryCategory category,
        AdvisoryLanguage language,
        List<String> cropCodes,
        LocalDate publishedAt,
        LocalDate validFrom,
        LocalDate validUntil,
        boolean verified
) {}