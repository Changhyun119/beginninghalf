package com.toy.attendance.dev.model.location.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.toy.attendance.dev.model.account.entity.Account;
import com.toy.attendance.dev.model.account.repository.AccountRepository;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto;
import com.toy.attendance.dev.model.attendance.entity.Attendance;
import com.toy.attendance.dev.model.attendance.repository.AttendanceRepository;
import com.toy.attendance.dev.model.location.dto.LocationDto;
import com.toy.attendance.dev.model.location.entity.Location;
import com.toy.attendance.dev.model.location.repository.LocationRepository;

@Transactional
@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final AttendanceRepository attendanceRepository;
    private final AccountRepository accountRepository;

    public LocationService(LocationRepository locationRepository, AttendanceRepository attendanceRepository, AccountRepository accountRepository) {
        this.locationRepository = locationRepository;
        this.attendanceRepository = attendanceRepository;
        this.accountRepository = accountRepository;
    }


    public ModelMap entry(LocationDto.locationForInsert request,HttpSession session) {
        ModelMap rModelMap = new ModelMap();
        
        try {
            if(this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }
            BigInteger accountId = (BigInteger) session.getAttribute("accountId");
            Location location = locationRepository.findByLocationNameAndAttendanceDateAndUseYn(request.getLocationName(),request.getAttendanceDate(),"Y");
            if (Objects.nonNull(location)) {
                System.out.println("Data Exists!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "해당 날짜에 이미 등록한 장소!");
                rModelMap.addAttribute("location", Collections.emptyList());
                return rModelMap;
            }

            location = locationRepository.save(
                Location.builder()
                .accountId(accountId)
                .locationName(request.getLocationName())
                .attendanceDate(request.getAttendanceDate())
                .official(request.getOfficial())
                .build()
            );

            if(Objects.isNull(location)){
                System.out.println("location not created");
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "location not created");
                rModelMap.addAttribute("location", Collections.emptyList());
                return rModelMap;
            }


            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("attendance", location);
        }
        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
            rModelMap.addAttribute("location", Collections.emptyList());
       }
       return rModelMap;
    }

    @Transactional
    public ModelMap change(LocationDto.locationForUpdate request, HttpSession session) {
        ModelMap rModelMap = new ModelMap();
    
        try {
            
            if(this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }

            Location location = locationRepository.findByLocationIdAndUseYn(request.getLocationId(),"Y");
            if (!location.getAccountId().equals(session.getAttribute("accountId"))) {
                System.out.println("등록한 사용자가 아님");
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "등록한 사용자가 아닙니다!");
                return rModelMap;
            }
            LocationDto.dsLocation locationDto = new LocationDto.dsLocation();
            locationDto.setLocationName(request.getLocationName());
            locationDto.setOfficial(request.getOfficial());
            location.updateLocation(locationDto);

            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("location", location);
         
        }

        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
            rModelMap.addAttribute("location", Collections.emptyList());
       }


       return rModelMap;

    }

    @Transactional
    public ModelMap cancel(LocationDto.locationForDelete request, HttpSession session) {
        ModelMap rModelMap = new ModelMap();
    
        try {
            if(this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }
            BigInteger accountId = (BigInteger) session.getAttribute("accountId");
            Location location = locationRepository.findByLocationIdAndUseYn(request.getLocationId(),"Y");
            Account account = accountRepository.findByAccountIdAndUseYn(accountId, "Y");
            if (!location.getAccountId().equals(accountId) && account.getAdminStatus().equals("N") ) {
                System.out.println("등록한 사용자가 아님, 관리자 아님");
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "등록한 사용자 및 관리자가 아닙니다!");
                rModelMap.addAttribute("location", Collections.emptyList());
                return rModelMap;
            }

            //2022-08-17 장소 삭제시 장소에 속한 사람들이 붕뜨는 오류 -> 장소삭제 전 해당 장소에 속한 인원 locationId = null 로 설정 후 삭제
            List<Attendance> attendanceList =  attendanceRepository.findByLocationIdAndUseYn(request.getLocationId() , "Y");

            AttendanceDto.dsAttendance attendanceDto = new AttendanceDto.dsAttendance();
            System.out.println(attendanceList);
            
            for (Attendance attendacneData : attendanceList) {
                System.out.println("장소에 속한 출석 데이터 ");
                System.out.println(attendacneData.getAttendanceId());
                attendacneData.setLocationId(null);
            }

            LocationDto.dsLocation locationDto = new LocationDto.dsLocation();
            locationDto.setUseYn("N");
            location.updateLocation(locationDto);

            rModelMap.addAttribute("success", "ok");
        }

        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");

       }


       return rModelMap;
    }
    
   
    public Boolean sessionIsNull(HttpSession session) {
        BigInteger accountId = (BigInteger) session.getAttribute("accountId");
            System.out.println("checkSessionNull@@");
            System.out.println(accountId);
            if(Objects.isNull(accountId) ) {
                return true;
            }
           
            return false;
    }
    public ModelMap failSessionCheck(ModelMap rModelMap) {
        rModelMap.addAttribute("success", "fail");
        rModelMap.addAttribute("reason", "유저 정보가 없습니다! 로그인 필요");
        return rModelMap;
    }


    @Transactional
    public ModelMap list(LocationDto.locationListRequest request) {
        ModelMap rModelMap = new ModelMap();
    
        try {
            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("locationList", locationRepository.findByAttendanceDateAndUseYn(request.getAttendanceDate(),"Y"));

        }
        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
            rModelMap.addAttribute("locationList", Collections.emptyList());

       }
       return rModelMap;
    }
    
    @Transactional
    public ModelMap sample() {
        ModelMap rModelMap = new ModelMap();
    
        try {
            rModelMap.addAttribute("success", "ok");
        }
        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");

       }


       return rModelMap;
    }

   
}
