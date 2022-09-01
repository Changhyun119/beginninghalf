package com.toy.attendance.dev.model.attendance.service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.toy.attendance.dev.model.account.service.AccountService;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto.attendanceForDelete;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto.attendanceForInsert;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto.attendanceForUpdate;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto.attendanceListRequestByYearAndMonth;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto.attendanceStatusListRequest;
import com.toy.attendance.dev.model.attendance.entity.Attendance;
import com.toy.attendance.dev.model.attendance.repository.AttendanceRepository;

import net.bytebuddy.utility.privilege.SetAccessibleAction;

@Transactional
@Service
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final AccountService accountService;

    public AttendanceService(AttendanceRepository attendanceRepository, AccountService accountService) {
        this.attendanceRepository = attendanceRepository;
        this.accountService = accountService;
    }

    @Transactional
    public ModelMap entry(attendanceForInsert request,HttpSession session) {
        ModelMap rModelMap = new ModelMap();
        
        try {
            if(this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }
            BigInteger accountId = (BigInteger) session.getAttribute("accountId");
            
            Attendance attendance = attendanceRepository.findByAccountIdAndAttendanceDateAndUseYn(accountId,request.getAttendanceDate(),"Y");
            if (Objects.nonNull(attendance)) {
                System.out.println("Data Exists!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "해당 날짜에 이미 등록한 유저!");
                rModelMap.addAttribute("attendance", Collections.emptyList());
                return rModelMap;
            }

            attendance = attendanceRepository.save(
                Attendance.builder()
                .accountId(accountId)
                .attendanceDate(request.getAttendanceDate())
                .mealStatus(request.getMealStatus())
                .locationId(request.getLocationId())
                .build()
            );

            if(Objects.isNull(attendance)){
                System.out.println("attendance not created");
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "attendance not created");
                rModelMap.addAttribute("attendance", Collections.emptyList());
                return rModelMap;
            }


            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("attendance", attendance);
        }
        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
            rModelMap.addAttribute("attendance", Collections.emptyList());
       }
       return rModelMap;
    }

    @Transactional
    public ModelMap change(attendanceForUpdate request, HttpSession session) {
        ModelMap rModelMap = new ModelMap();
    
        try {
            
            if(this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }

            Attendance attendance = attendanceRepository.findByAttendanceIdAndUseYn(request.getAttendanceId(),"Y");
            if (!attendance.getAccountId().equals(session.getAttribute("accountId"))) {
                System.out.println("등록한 사용자가 아님");
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "등록한 사용자가 아닙니다!");
                rModelMap.addAttribute("attendance", Collections.emptyList());
                return rModelMap;
            }
            AttendanceDto.dsAttendance attendanceDto = new AttendanceDto.dsAttendance();
            attendanceDto.setAttendanceDate(request.getAttendanceDate());
            attendanceDto.setMealStatus(request.getMealStatus());
            attendance.setLocationId(request.getLocationId());
            attendance.updateAttendance(attendanceDto);

            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("attendance", attendance);
         
        }


        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
            rModelMap.addAttribute("attendance", Collections.emptyList());
       }


       return rModelMap;

    }

    @Transactional
    public ModelMap cancel(attendanceForDelete request, HttpSession session) {
        ModelMap rModelMap = new ModelMap();
    
        try {
            if(this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }

            Attendance attendance = attendanceRepository.findByAttendanceIdAndUseYn(request.getAttendanceId(),"Y");
            if (!attendance.getAccountId().equals(session.getAttribute("accountId"))) {
                System.out.println("출석 안의 사용자 ID : ");
                System.out.println(attendance.getAccountId());
                System.out.println("session 값 :");
                System.out.println(session.getAttribute("accountId"));
                System.out.println("등록한 사용자가 아님");
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "등록한 사용자가 아닙니다!");
                rModelMap.addAttribute("attendance", Collections.emptyList());
                return rModelMap;
            }
            AttendanceDto.dsAttendance attendanceDto = new AttendanceDto.dsAttendance();
            attendanceDto.setUseYn("N");
            attendance.updateAttendance(attendanceDto);

            

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
    public ModelMap list(AttendanceDto.attendanceListRequest request) {
        ModelMap rModelMap = new ModelMap();
    
        try {
            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("attendanceList", attendanceRepository.findAllList(request));

        }
        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
            rModelMap.addAttribute("attendanceList", Collections.emptyList());

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

    public ModelMap getMyAttendanceCount(HttpSession session) {
        ModelMap rModelMap = new ModelMap();
        LocalDate now = LocalDate.now();
        try {
            if(this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }
            BigInteger accountId = (BigInteger) session.getAttribute("accountId");
            String yearAndMonth = now.toString().split("-")[0] + "-" + now.toString().split("-")[1];
            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("myAttendance", attendanceRepository.findMyAttendance(accountId, yearAndMonth));

        }
        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");

       }


       return rModelMap;
    }

    
    @Transactional
    public ModelMap getAttendanceStatusByYearAndMonth(attendanceStatusListRequest request, HttpSession session) {
        ModelMap rModelMap = new ModelMap();
    
        try {
            if(this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }
            if (!session.getAttribute("adminStatus").toString().equals("Y") ) {
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "관리자가 아닌 사용자 계정 입니다.");
                
                return rModelMap;
            }
            

            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("attendanceList", attendanceRepository.findAllAttendanceStatusList(request));

        }
        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
            rModelMap.addAttribute("attendanceList", Collections.emptyList());

       }
       return rModelMap;
    }

}
