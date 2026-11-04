package com.krishimitra.market.mapper;

import com.krishimitra.market.client.DataGovMarketRecord;
import com.krishimitra.market.dto.response.MarketPriceResponse;
import com.krishimitra.market.enums.MarketDataSource;
import com.krishimitra.market.model.MarketPrice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

@Component
public class MarketPriceMapper {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("d/M/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ISO_LOCAL_DATE
    );

    public MarketPriceResponse toResponse(MarketPrice price) {
        return new MarketPriceResponse(
                price.getId(),
                price.getState(),
                price.getDistrict(),
                price.getMarket(),
                price.getCommodity(),
                price.getVariety(),
                price.getGrade(),
                price.getArrivalDate(),
                price.getMinPrice(),
                price.getMaxPrice(),
                price.getModalPrice(),
                price.getPriceUnit(),
                price.getSource(),
                price.getFetchedAt()
        );
    }

    public MarketPrice toEntity(DataGovMarketRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Market record cannot be null");
        }

        MarketPrice price = new MarketPrice();

        price.setState(requireText(record.state(), "state"));
        price.setDistrict(requireText(record.district(), "district"));
        price.setMarket(requireText(record.market(), "market"));
        price.setCommodity(requireText(record.commodity(), "commodity"));

        price.setVariety(normalizeOptionalText(record.variety()));
        price.setGrade(normalizeOptionalText(record.grade()));

        price.setArrivalDate(parseDate(record.arrivalDate()));

        BigDecimal minPrice = parsePrice(record.minPrice(), "minPrice");
        BigDecimal maxPrice = parsePrice(record.maxPrice(), "maxPrice");
        BigDecimal modalPrice = parsePrice(record.modalPrice(), "modalPrice");

        validatePrices(minPrice, maxPrice, modalPrice);

        price.setMinPrice(minPrice);
        price.setMaxPrice(maxPrice);
        price.setModalPrice(modalPrice);

        // Data.gov.in mandi dataset reports prices in ₹ per quintal.
        price.setPriceUnit("INR/QUINTAL");

        price.setSource(MarketDataSource.DATA_GOV_IN);

        return price;
    }

    private String requireText(String value, String field) {
        String normalized = normalizeOptionalText(value);

        if (normalized == null) {
            throw new IllegalArgumentException(
                    "Required field is missing: " + field
            );
        }

        return normalized;
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim().replaceAll("\\s+", " ");
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Arrival date is missing");
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(value.trim(), formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }

        throw new IllegalArgumentException(
                "Invalid arrival date: " + value
        );
    }

    private BigDecimal parsePrice(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing price field: " + field
            );
        }

        try {
            BigDecimal price = new BigDecimal(
                    value.trim().replace(",", "")
            );

            if (price.signum() < 0) {
                throw new IllegalArgumentException(
                        "Price cannot be negative: " + field
                );
            }

            return price;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid price value for " + field + ": " + value,
                    exception
            );
        }
    }

    private void validatePrices(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal modalPrice
    ) {
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException(
                    "Minimum price cannot exceed maximum price"
            );
        }

        if (modalPrice.compareTo(minPrice) < 0
                || modalPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException(
                    "Modal price must be between minimum and maximum prices"
            );
        }
    }
}