package com.toy.attendance.dev.model.attendance.repository;



import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.toy.attendance.dev.model.attendance.entity.Attendance;


public interface AttendanceRepository extends JpaRepository<Attendance, Long>, AttendanceRepositoryCustom{

    Attendance findByAccountIdAndAttendanceDateAndUseYn(BigInteger account_id, Date attendanceDate, String useYn);

    Attendance findByAttendanceIdAndUseYn(BigInteger attendanceId, String useYn);

    List<Attendance> findAllByUseYn(String useYn);

    List<Attendance> findByLocationIdAndUseYn(BigInteger locationId, String useYn);

    @Query("select count(attnd) from Attendance attnd  where attnd.accountId = :accountId AND to_char(attnd.attendanceDate,'YYYY-MM-DD') like  %:yearAndMonth% AND attnd.useYn ='Y'")
    BigInteger findMyAttendance(@Param("accountId") BigInteger accountId,@Param("yearAndMonth") String yearAndMonth);
      
}
