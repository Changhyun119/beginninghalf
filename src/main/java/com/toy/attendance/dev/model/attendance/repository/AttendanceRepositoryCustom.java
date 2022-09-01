package com.toy.attendance.dev.model.attendance.repository;

import java.math.BigInteger;
import java.util.List;

import com.toy.attendance.dev.model.attendance.dto.AttendanceDto;


public interface AttendanceRepositoryCustom {
    
    public List<AttendanceDto.attendanceListResponse> findAllList(AttendanceDto.attendanceListRequest request) throws Exception;

    public List<AttendanceDto.attendanceStatusListResponse> findAllAttendanceStatusList(AttendanceDto.attendanceStatusListRequest request) throws Exception;
}
