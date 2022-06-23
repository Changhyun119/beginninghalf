package com.toy.attendance.dev.model.attendance.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/atndn")
@Api(tags = "#2.Attendance Controller", value = "/", description = "")
public class AttendanceController {
    
    public AttendanceController() {

    }
}
