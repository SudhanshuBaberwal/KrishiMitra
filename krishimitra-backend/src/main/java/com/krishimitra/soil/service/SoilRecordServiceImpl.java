package com.krishimitra.soil.service;

import com.krishimitra.farmer.model.FarmerProfile;
import com.krishimitra.farmer.repository.FarmerRepository;
import com.krishimitra.soil.dto.request.CreateSoilRecordRequest;
import com.krishimitra.soil.dto.request.UpdateSoilRecordRequest;
import com.krishimitra.soil.dto.response.SoilRecordResponse;
import com.krishimitra.soil.enums.SoilDataSource;
import com.krishimitra.soil.enums.SoilVerificationStatus;
import com.krishimitra.soil.exception.SoilFarmerProfileNotFoundException;
import com.krishimitra.soil.exception.SoilRecordNotFoundException;
import com.krishimitra.soil.model.SoilParameters;
import com.krishimitra.soil.model.SoilRecord;
import com.krishimitra.soil.repository.SoilRecordRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SoilRecordServiceImpl implements SoilRecordService {

    private final SoilRecordRepository soilRecordRepository;
    private final FarmerRepository farmerRepository;

    public SoilRecordServiceImpl(
            SoilRecordRepository soilRecordRepository,
            FarmerRepository farmerRepository
    ) {
        this.soilRecordRepository = soilRecordRepository;
        this.farmerRepository = farmerRepository;
    }

    @Override
    public SoilRecordResponse createSoilRecord(
            CreateSoilRecordRequest request,
            String userId
    ) {
        FarmerProfile farmer = getFarmerProfile(userId);

        SoilRecord record = new SoilRecord();
        record.setFarmerId(farmer.getId());
        record.setUserId(userId);
        record.setSampleDate(request.sampleDate());
        record.setSampleLocation(request.sampleLocation());
        record.setSource(request.source());
        record.setVerificationStatus(
                request.source() == SoilDataSource.FARMER_ENTERED
                        ? SoilVerificationStatus.SELF_REPORTED
                        : SoilVerificationStatus.PENDING_VERIFICATION
        );
        record.setLabName(request.labName());
        record.setReportReference(request.reportReference());
        record.setParameters(mapParameters(request.parameters()));
        record.setNotes(request.notes());
        record.setActive(true);

        Instant now = Instant.now();
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        return mapToResponse(soilRecordRepository.save(record));
    }

    @Override
    public List<SoilRecordResponse> getMySoilRecords(String userId) {
        FarmerProfile farmer = getFarmerProfile(userId);

        return soilRecordRepository
                .findByFarmerIdAndActiveTrueOrderBySampleDateDesc(farmer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SoilRecordResponse getSoilRecordById(
            String recordId,
            String userId
    ) {
        return mapToResponse(getOwnedRecord(recordId, userId));
    }

    @Override
    public SoilRecordResponse updateSoilRecord(
            String recordId,
            UpdateSoilRecordRequest request,
            String userId
    ) {
        SoilRecord record = getOwnedRecord(recordId, userId);

        if (request.sampleLocation() != null) {
            record.setSampleLocation(request.sampleLocation());
        }

        if (request.labName() != null) {
            record.setLabName(request.labName());
        }

        if (request.reportReference() != null) {
            record.setReportReference(request.reportReference());
        }

        if (request.parameters() != null) {
            record.setParameters(mapParameters(request.parameters()));
        }

        if (request.notes() != null) {
            record.setNotes(request.notes());
        }

        record.setUpdatedAt(Instant.now());

        return mapToResponse(soilRecordRepository.save(record));
    }

    @Override
    public void deleteSoilRecord(String recordId, String userId) {
        SoilRecord record = getOwnedRecord(recordId, userId);
        record.setActive(false);
        record.setUpdatedAt(Instant.now());
        soilRecordRepository.save(record);
    }

    private FarmerProfile getFarmerProfile(String userId) {
        return farmerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new SoilFarmerProfileNotFoundException(
                                "Farmer profile not found. Please create your profile first."
                        )
                );
    }

    private SoilRecord getOwnedRecord(String recordId, String userId) {
        return soilRecordRepository
                .findByIdAndUserIdAndActiveTrue(recordId, userId)
                .orElseThrow(() ->
                        new SoilRecordNotFoundException(
                                "Soil record not found with id: " + recordId
                        )
                );
    }

    private SoilParameters mapParameters(
            CreateSoilRecordRequest.SoilParameterRequest request
    ) {
        SoilParameters parameters = new SoilParameters();

        parameters.setPH(request.pH());
        parameters.setElectricalConductivity(request.electricalConductivity());
        parameters.setOrganicCarbon(request.organicCarbon());
        parameters.setNitrogen(request.nitrogen());
        parameters.setPhosphorus(request.phosphorus());
        parameters.setPotassium(request.potassium());
        parameters.setSulphur(request.sulphur());
        parameters.setZinc(request.zinc());
        parameters.setIron(request.iron());
        parameters.setManganese(request.manganese());
        parameters.setCopper(request.copper());
        parameters.setBoron(request.boron());

        return parameters;
    }

    private SoilRecordResponse mapToResponse(SoilRecord record) {
        return new SoilRecordResponse(
                record.getId(),
                record.getFarmerId(),
                record.getSampleDate(),
                record.getSampleLocation(),
                record.getSource(),
                record.getVerificationStatus(),
                record.getLabName(),
                record.getReportReference(),
                record.getParameters(),
                record.getNotes(),
                record.isActive(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}