package com.krishimitra.advisory.service;

import com.krishimitra.advisory.dto.AdvisorySearchRequest;
import com.krishimitra.advisory.dto.request.CreateAdvisoryRequest;
import com.krishimitra.advisory.dto.response.AdvisoryResponse;

import java.util.List;

public interface OfficialAdvisoryService {

    AdvisoryResponse publishAdvisory(CreateAdvisoryRequest request);

    List<AdvisoryResponse> getFarmerAdvisories(
            String userId,
            AdvisorySearchRequest request
    );

    AdvisoryResponse getAdvisoryById(String id, String userId);
}