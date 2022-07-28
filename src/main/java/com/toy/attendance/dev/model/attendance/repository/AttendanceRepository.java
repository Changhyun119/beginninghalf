package com.toy.attendance.dev.model.attendance.repository;



import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.toy.attendance.dev.model.attendance.entity.Attendance;


public interface AttendanceRepository extends JpaRepository<Attendance, Long>, AttendanceRepositoryCustom{

    Attendance findByAccountIdAndAttendanceDateAndUseYn(Long account_id, Date attendanceDate, String string);

    Attendance findByAttendanceIdAndUseYn(Long attendanceId, String string);

    List<Attendance> findAllByUseYn(String string);

    
   
}
