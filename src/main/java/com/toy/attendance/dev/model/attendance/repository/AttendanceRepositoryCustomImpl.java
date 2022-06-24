package com.toy.attendance.dev.model.attendance.repository;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.toy.attendance.dev.model.attendance.entity.Attendance;

public class AttendanceRepositoryCustomImpl extends QuerydslRepositorySupport implements AttendanceRepositoryCustom{

    public AttendanceRepositoryCustomImpl() {
        super(Attendance.class);
        //TODO Auto-generated constructor stub
    }
    
}
