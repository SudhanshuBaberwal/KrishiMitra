
package com.krishimitra.schedule.service;

import com.krishimitra.crop.entity.FarmerCrop;
import com.krishimitra.crop.repository.FarmerCropRepository;
import com.krishimitra.schedule.dto.request.CreateScheduleRequest;
import com.krishimitra.schedule.dto.request.UpdateScheduleRequest;
import com.krishimitra.schedule.dto.request.UpdateScheduleStatusRequest;
import com.krishimitra.schedule.dto.response.CropScheduleResponse;
import com.krishimitra.schedule.entity.CropSchedule;
import com.krishimitra.schedule.enums.ScheduleStatus;
import com.krishimitra.schedule.exception.ScheduleCropNotFoundException;
import com.krishimitra.schedule.exception.ScheduleNotFoundException;
import com.krishimitra.schedule.repository.CropScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class CropScheduleServiceImpl implements CropScheduleService {

    private static final ZoneId INDIA_ZONE =
            ZoneId.of("Asia/Kolkata");

    private final CropScheduleRepository scheduleRepository;
    private final FarmerCropRepository farmerCropRepository;

    public CropScheduleServiceImpl(
            CropScheduleRepository scheduleRepository,
            FarmerCropRepository farmerCropRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.farmerCropRepository = farmerCropRepository;
    }

    @Override
    public CropScheduleResponse createSchedule(
            String cropId,
            CreateScheduleRequest request,
            String userId
    ) {
        FarmerCrop crop = findActiveCrop(cropId, userId);

        CropSchedule schedule = new CropSchedule();

        schedule.setFarmerId(crop.getFarmerId());
        schedule.setUserId(userId);
        schedule.setCropId(crop.getId());
        schedule.setCropName(crop.getCropName());

        schedule.setTitle(request.title());
        schedule.setDescription(request.description());
        schedule.setActivityType(request.activityType());
        schedule.setScheduledDate(request.scheduledDate());
        schedule.setStatus(ScheduleStatus.PENDING);
        schedule.setNotes(request.notes());

        schedule.setActive(true);

        Instant now = Instant.now();
        schedule.setCreatedAt(now);
        schedule.setUpdatedAt(now);

        return toResponse(scheduleRepository.save(schedule));
    }

    @Override
    public List<CropScheduleResponse> getSchedules(
            String userId,
            ScheduleStatus status,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        if ((fromDate == null) != (toDate == null)) {
            throw new IllegalArgumentException(
                    "Both fromDate and toDate must be provided together"
            );
        }

        if (fromDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "fromDate cannot be after toDate"
            );
        }

        List<CropSchedule> schedules;

        if (fromDate != null && status != null) {
            schedules = scheduleRepository
                    .findByUserIdAndActiveTrueAndStatusAndScheduledDateBetweenOrderByScheduledDateAsc(
                            userId, status, fromDate, toDate
                    );
        } else if (fromDate != null) {
            schedules = scheduleRepository
                    .findByUserIdAndActiveTrueAndScheduledDateBetweenOrderByScheduledDateAsc(
                            userId, fromDate, toDate
                    );
        } else if (status != null) {
            schedules = scheduleRepository
                    .findByUserIdAndActiveTrueAndStatusOrderByScheduledDateAsc(
                            userId, status
                    );
        } else {
            schedules = scheduleRepository
                    .findByUserIdAndActiveTrueOrderByScheduledDateAsc(
                            userId
                    );
        }

        return schedules.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CropScheduleResponse> getUpcomingSchedules(
            String userId
    ) {
        LocalDate today = LocalDate.now(INDIA_ZONE);

        return scheduleRepository
                .findByUserIdAndActiveTrueAndStatusAndScheduledDateGreaterThanEqualOrderByScheduledDateAsc(
                        userId,
                        ScheduleStatus.PENDING,
                        today
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CropScheduleResponse> getSchedulesByCropId(
            String cropId,
            String userId
    ) {
        findActiveCrop(cropId, userId);

        return scheduleRepository
                .findByUserIdAndCropIdAndActiveTrueOrderByScheduledDateAsc(
                        userId,
                        cropId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CropScheduleResponse getScheduleById(
            String id,
            String userId
    ) {
        return toResponse(findSchedule(id, userId));
    }

    @Override
    public CropScheduleResponse updateSchedule(
            String id,
            UpdateScheduleRequest request,
            String userId
    ) {
        CropSchedule schedule = findSchedule(id, userId);

        schedule.setTitle(request.title());
        schedule.setDescription(request.description());
        schedule.setActivityType(request.activityType());
        schedule.setScheduledDate(request.scheduledDate());
        schedule.setNotes(request.notes());

        schedule.setUpdatedAt(Instant.now());

        return toResponse(scheduleRepository.save(schedule));
    }

    @Override
    public CropScheduleResponse updateScheduleStatus(
            String id,
            UpdateScheduleStatusRequest request,
            String userId
    ) {
        CropSchedule schedule = findSchedule(id, userId);

        ScheduleStatus currentStatus = schedule.getStatus();
        ScheduleStatus newStatus = request.status();

        // Repeating the same status is allowed.
        if (currentStatus == newStatus) {
            return toResponse(schedule);
        }

        // Completed and skipped schedules are terminal.
        if (currentStatus != ScheduleStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Completed or skipped schedules cannot be changed"
            );
        }

        schedule.setStatus(newStatus);

        if (newStatus == ScheduleStatus.COMPLETED) {
            schedule.setCompletedDate(
                    LocalDate.now(INDIA_ZONE)
            );
        } else {
            schedule.setCompletedDate(null);
        }

        schedule.setUpdatedAt(Instant.now());

        return toResponse(scheduleRepository.save(schedule));
    }

    @Override
    public void deleteSchedule(
            String id,
            String userId
    ) {
        CropSchedule schedule = findSchedule(id, userId);

        schedule.setActive(false);
        schedule.setUpdatedAt(Instant.now());

        scheduleRepository.save(schedule);
    }

    private FarmerCrop findActiveCrop(
            String cropId,
            String userId
    ) {
        return farmerCropRepository
                .findByIdAndUserId(cropId, userId)
                .filter(FarmerCrop::isActive)
                .orElseThrow(() ->
                        new ScheduleCropNotFoundException(
                                "Active crop not found for this farmer"
                        )
                );
    }

    private CropSchedule findSchedule(
            String id,
            String userId
    ) {
        return scheduleRepository
                .findByIdAndUserIdAndActiveTrue(id, userId)
                .orElseThrow(() ->
                        new ScheduleNotFoundException(
                                "Schedule not found"
                        )
                );
    }

    private CropScheduleResponse toResponse(
            CropSchedule schedule
    ) {
        return new CropScheduleResponse(
                schedule.getId(),
                schedule.getFarmerId(),
                schedule.getCropId(),
                schedule.getCropName(),
                schedule.getTitle(),
                schedule.getDescription(),
                schedule.getActivityType(),
                schedule.getScheduledDate(),
                schedule.getStatus(),
                schedule.getCompletedDate(),
                schedule.getNotes(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }
}
