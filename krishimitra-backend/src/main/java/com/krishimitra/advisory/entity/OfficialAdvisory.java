package com.krishimitra.advisory.entity;

import com.krishimitra.advisory.enums.AdvisoryCategory;
import com.krishimitra.advisory.enums.AdvisoryLanguage;
import com.krishimitra.advisory.enums.AdvisorySource;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "official_advisories")
@CompoundIndex(
        name = "district_active_validity_idx",
        def = "{'district': 1, 'active': 1, 'validUntil': 1}"
)
public class OfficialAdvisory {

    @Id
    private String id;

    @Indexed
    private String state;

    @Indexed
    private String district;

    private AdvisorySource source;

    private String sourceName;

    private String sourceReference;

    private String sourceUrl;

    private String title;

    private String summary;

    private String content;

    private AdvisoryCategory category;

    private AdvisoryLanguage language;

    private List<String> cropCodes;

    private LocalDate publishedAt;

    private LocalDate validFrom;

    private LocalDate validUntil;

    private boolean verified;

    private boolean active;

    private Instant createdAt;

    private Instant updatedAt;
}