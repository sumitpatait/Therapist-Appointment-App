package com.tapp.therapistappointmentapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tapp.therapistappointmentapp.dto.ScheduleRequest;
import com.tapp.therapistappointmentapp.dto.ScheduleResponse;
import com.tapp.therapistappointmentapp.model.Schedule;
import com.tapp.therapistappointmentapp.model.Therapist;
import com.tapp.therapistappointmentapp.repository.ScheduleRepository;
import com.tapp.therapistappointmentapp.repository.TherapistRepository; // Needed to find Therapist by ID

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TherapistRepository therapistRepository; // Inject TherapistRepository

    public ScheduleService(ScheduleRepository scheduleRepository, TherapistRepository therapistRepository) {
        this.scheduleRepository = scheduleRepository;
        this.therapistRepository = therapistRepository;
    }

    @Transactional
    public ScheduleResponse addTherapistSchedule(Long therapistId, ScheduleRequest request) {
        // Find the therapist to link the schedule to
        Therapist therapist = therapistRepository.findById(therapistId)
                .orElseThrow(() -> new IllegalArgumentException("Therapist not found with ID: " + therapistId));

        // Check for overlapping or duplicate schedule entries (optional, but good practice)
        if (scheduleRepository.findByTherapistIdAndDayOfWeekAndStartTimeAndEndTime(
            therapistId, request.getDayOfWeek(), request.getStartTime(), request.getEndTime()
        ).isPresent()) {
            throw new IllegalArgumentException("Schedule slot already exists for this therapist on this day and time.");
        }
        
        // Basic validation: ensure start time is before end time
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }


        Schedule schedule = new Schedule();
        schedule.setTherapist(therapist); // Link to the Therapist entity
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return convertToResponse(savedSchedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getTherapistSchedules(Long therapistId) {
        // Find the therapist to ensure they exist before querying their schedules
        if (!therapistRepository.existsById(therapistId)) {
            throw new IllegalArgumentException("Therapist not found with ID: " + therapistId);
        }
        List<Schedule> schedules = scheduleRepository.findByTherapistId(therapistId);
        return schedules.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, Long therapistId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));

        // Ensure the schedule belongs to the specified therapist for security
        if (!schedule.getTherapist().getId().equals(therapistId)) {
            throw new SecurityException("Unauthorized: Schedule does not belong to this therapist.");
        }

        scheduleRepository.delete(schedule);
    }

    private ScheduleResponse convertToResponse(Schedule schedule) {
        ScheduleResponse response = new ScheduleResponse();
        response.setId(schedule.getId());
        response.setTherapistId(schedule.getTherapist().getId());
        response.setDayOfWeek(schedule.getDayOfWeek());
        response.setStartTime(schedule.getStartTime());
        response.setEndTime(schedule.getEndTime());
        return response;
    }
}