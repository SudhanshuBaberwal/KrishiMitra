package com.krishimitra.soil.service;

import com.krishimitra.soil.dto.request.CreateSoilRecordRequest;
import com.krishimitra.soil.dto.request.UpdateSoilRecordRequest;
import com.krishimitra.soil.dto.response.SoilRecordResponse;

import java.util.List;

public interface SoilRecordService {

    SoilRecordResponse createSoilRecord(
            CreateSoilRecordRequest request,
            String userId
    );

    List<SoilRecordResponse> getMySoilRecords(String userId);

    SoilRecordResponse getSoilRecordById(
            String recordId,
            String userId
    );

    SoilRecordResponse updateSoilRecord(
            String recordId,
            UpdateSoilRecordRequest request,
            String userId
    );

    void deleteSoilRecord(String recordId, String userId);
}