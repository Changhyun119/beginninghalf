package com.toy.attendance.dev.model.attendance.controller;


import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toy.attendance.dev.model.attendance.dto.AttendanceDto;
import com.toy.attendance.dev.model.attendance.service.AttendanceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/atndn")
@Api(tags = "#2.Attendance Controller", value = "/", description = "")
public class AttendanceController {
    
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;

    }

    @PostMapping("/entry")
    @ApiOperation(value="출석 등록 API", notes="mealStatus : (\"Y\",\"N\") , attendanceDate : \"YYYY-MM-DD\" 형식 필수 ")
    public ResponseEntity<ModelMap> entryAttendance(@RequestBody AttendanceDto.attendanceForInsert request, HttpSession session) throws Exception{
        ModelMap rModelMap = attendanceService.entry(request, session);
        
        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/change")
    @ApiOperation(value="출석 변경 API", notes="mealStatus : (\"Y\",\"N\") , attendanceDate : \"YYYY-MM-DD\" 형식 필수 ")
    public ResponseEntity<ModelMap> changeAttendance(@RequestBody AttendanceDto.attendanceForUpdate request, HttpSession session) throws Exception{
        ModelMap rModelMap = attendanceService.change(request, session);
        
        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/cancel")
    @ApiOperation(value="출석 취소 API", notes=" ")
    public ResponseEntity<ModelMap> cancelAttendance(@RequestBody AttendanceDto.attendanceForDelete request, HttpSession session) throws Exception{
        ModelMap rModelMap = attendanceService.cancel(request, session);
        
        return ResponseEntity.ok(rModelMap);
    }

    @GetMapping("/list")
    @ApiOperation(value="출석 목록 API", notes=" ")
    public ResponseEntity<ModelMap> getAttendance() throws Exception{
        AttendanceDto.attendanceListRequest request = new AttendanceDto.attendanceListRequest();
        ModelMap rModelMap = attendanceService.list(request);
        
        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/list-on-date")
    @ApiOperation(value="특정 날짜 출석 API", notes="Parameter : {'attendanceDate' : 'YYYY-MM-DD'} ")
    public ResponseEntity<ModelMap> getAttendanceByAttendaceDate(@RequestBody AttendanceDto.attendanceListRequest request) throws Exception{
        ModelMap rModelMap = attendanceService.list(request);
        
        return ResponseEntity.ok(rModelMap);
    }
}
