package com.tapp.therapistappointmentapp.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tapp.therapistappointmentapp.model.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // Find all schedule slots for a specific therapist
    List<Schedule> findByTherapistId(Long therapistId);

    // Find a specific schedule slot for a therapist by day and time
    Optional<Schedule> findByTherapistIdAndDayOfWeekAndStartTimeAndEndTime(Long therapistId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);
}