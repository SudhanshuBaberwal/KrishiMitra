package com.krishimitra.advisory.dto.request;

import com.krishimitra.advisory.enums.AdvisoryCategory;
import com.krishimitra.advisory.enums.AdvisoryLanguage;
import com.krishimitra.advisory.enums.AdvisorySource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.List;

public record CreateAdvisoryRequest(

        @NotBlank
        String state,

        @NotBlank
        String district,

        @NotNull
        AdvisorySource source,

        @NotBlank
        String sourceName,

        String sourceReference,

        @NotBlank
        @URL
        String sourceUrl,

        @NotBlank
        @Size(max = 200)
        String title,

        @NotBlank
        @Size(max = 500)
        String summary,

        @NotBlank
        String content,

        @NotNull
        AdvisoryCategory category,

        @NotNull
        AdvisoryLanguage language,

        List<String> cropCodes,

        @NotNull
        LocalDate publishedAt,

        @NotNull
        LocalDate validFrom,

        @NotNull
        LocalDate validUntil
) {}