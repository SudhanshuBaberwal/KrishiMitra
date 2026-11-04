package com.krishimitra.market.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriBuilder;

import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataGovClient {

    private static final String BASE_URL = "https://api.data.gov.in";

    private final RestClient restClient;
    private final String apiKey;
    private final String resourceId;
    private final int pageSize;

    public DataGovClient(
            @Value("${app.market.data-gov-api-key}") String apiKey,
            @Value("${app.market.data-gov-resource-id}") String resourceId,
            @Value("${app.market.data-gov-page-size:100}") int pageSize
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();

        this.apiKey = apiKey;
        this.resourceId = resourceId;
        this.pageSize = pageSize;

        if (pageSize < 1 || pageSize > 1000) {
            throw new IllegalArgumentException(
                    "Data.gov.in page size must be between 1 and 1000"
            );
        }
    }

    /**
     * Fetch one page of market records.
     */
    public DataGovPage fetchPage(
            String state,
            String district,
            int offset,
            Integer requestedLimit
    ) {
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State is required");
        }

        if (district == null || district.isBlank()) {
            throw new IllegalArgumentException("District is required");
        }

        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }

        int limit = requestedLimit == null ? pageSize : requestedLimit;

        if (limit < 1 || limit > 1000) {
            throw new IllegalArgumentException(
                    "Limit must be between 1 and 1000"
            );
        }

        JsonNode response;

        try {
            response = restClient.get()
                    .uri(uriBuilder -> buildUri(
                            uriBuilder,
                            state,
                            district,
                            offset,
                            limit
                    ))
                    .retrieve()
                    .body(JsonNode.class);

        } catch (RestClientResponseException exception) {
            throw new IllegalStateException(
                    "Data.gov.in returned HTTP status "
                            + exception.getStatusCode().value(),
                    exception
            );

        } catch (ResourceAccessException exception) {
            throw new IllegalStateException(
                    "Unable to connect to Data.gov.in. Check network or API availability.",
                    exception
            );
        }

        if (response == null) {
            throw new IllegalStateException(
                    "Data.gov.in returned an empty response"
            );
        }

        JsonNode recordsNode = response.get("records");

        if (recordsNode == null || !recordsNode.isArray()) {
            throw new IllegalStateException(
                    "Invalid Data.gov.in response: records array is missing"
            );
        }

        List<DataGovMarketRecord> records = new ArrayList<>();

        for (JsonNode record : recordsNode) {
            records.add(mapRecord(record));
        }

        long totalRecords = parseTotalRecords(response);

        return new DataGovPage(
                List.copyOf(records),
                totalRecords,
                offset,
                limit
        );
    }

    /**
     * Fetch all available records for a state and district.
     * Pagination is controlled by the API's total count.
     */
    public List<DataGovMarketRecord> fetchAll(
            String state,
            String district
    ) {
        List<DataGovMarketRecord> allRecords = new ArrayList<>();

        int offset = 0;
        long totalRecords;

        do {
            DataGovPage page = fetchPage(
                    state,
                    district,
                    offset,
                    pageSize
            );

            allRecords.addAll(page.records());

            totalRecords = page.totalRecords();

            if (page.records().isEmpty()) {
                break;
            }

            offset += page.records().size();

        } while (offset < totalRecords);

        return List.copyOf(allRecords);
    }

    private java.net.URI buildUri(
            UriBuilder uriBuilder,
            String state,
            String district,
            int offset,
            int limit
    ) {
        return uriBuilder
                .path("/resource/{resourceId}")
                .queryParam("api-key", apiKey)
                .queryParam("format", "json")
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .queryParam("filters[state]", state)
                .queryParam("filters[district]", district)
                .build(resourceId);
    }

    private DataGovMarketRecord mapRecord(JsonNode node) {
        return new DataGovMarketRecord(
                readText(node, "state"),
                readText(node, "district"),
                readText(node, "market"),
                readText(node, "commodity"),
                readText(node, "variety"),
                readText(node, "grade"),
                readText(node, "arrival_date"),
                readText(node, "min_price"),
                readText(node, "max_price"),
                readText(node, "modal_price")
        );
    }

    private String readText(JsonNode node, String field) {
        JsonNode value = node.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        String text = value.asText();

        return text == null || text.isBlank() ? null : text.trim();
    }

    private long parseTotalRecords(JsonNode response) {
        JsonNode totalNode = response.get("total");

        if (totalNode == null || totalNode.isNull()) {
            return 0;
        }

        try {
            return Long.parseLong(totalNode.asText());
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "Invalid total count received from Data.gov.in",
                    exception
            );
        }
    }
}