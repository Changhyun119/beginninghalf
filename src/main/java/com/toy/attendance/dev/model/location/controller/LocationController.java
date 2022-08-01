package com.toy.attendance.dev.model.location.controller;


import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.toy.attendance.dev.model.location.dto.LocationDto;
import com.toy.attendance.dev.model.location.service.LocationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/loc")
@Api(tags = "#3.Location Controller", value = "/loc", description = "")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;

    }
    
    @PostMapping("/entry")
    @ApiOperation(value="장소 등록 API", notes="locationName : String ,official : (\"Y\",\"N\") , attendanceDate : \"YYYY-MM-DD\" 형식 필수 ")
    public ResponseEntity<ModelMap> entryLocation(@RequestBody LocationDto.locationForInsert request, HttpSession session) throws Exception{
        ModelMap rModelMap = locationService.entry(request, session);
        
        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/change")
    @ApiOperation(value="장소 변경 API", notes="official : (\"Y\",\"N\") , attendanceDate : \"YYYY-MM-DD\" 형식 필수 ")
    public ResponseEntity<ModelMap> changeLocation(@RequestBody LocationDto.locationForUpdate request, HttpSession session) throws Exception{
        ModelMap rModelMap = locationService.change(request, session);
        
        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/cancel")
    @ApiOperation(value="장소 취소 API", notes=" ")
    public ResponseEntity<ModelMap> cancelLocation(@RequestBody LocationDto.locationForDelete request, HttpSession session) throws Exception{
        ModelMap rModelMap = locationService.cancel(request, session);
        
        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/list")
    @ApiOperation(value="장소 목록 API", notes=" ")
    public ResponseEntity<ModelMap> getLocation(@RequestBody LocationDto.locationListRequest request) throws Exception{
        
        ModelMap rModelMap = locationService.list(request);
        
        return ResponseEntity.ok(rModelMap);
    }
    @PostMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public void test() {
       System.out.println(new BigInteger("0"));
       Object a = null;
       BigInteger b = (BigInteger) a;
       System.out.println(b);
    }


}
