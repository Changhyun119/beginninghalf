package com.toy.attendance.dev.model.attendance.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.toy.attendance.dev.model.account.service.AccountService;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto.attendanceForDelete;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto.attendanceForInsert;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto.attendanceForUpdate;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto.attendanceStatusListRequest;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto.attendanceStatusListResponse;
import com.toy.attendance.dev.model.attendance.entity.Attendance;
import com.toy.attendance.dev.model.attendance.repository.AttendanceRepository;
import com.toy.attendance.dev.util.CalendarUtil;
import com.toy.attendance.dev.util.FileTransferHandler;
import com.toy.attendance.dev.util.StringUtills;

@Transactional
@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AccountService accountService;

    private String excelRootPath = System.getProperty("user.dir") + File.separator + "excel" + File.separator;

    public AttendanceService(AttendanceRepository attendanceRepository, AccountService accountService) {
        this.attendanceRepository = attendanceRepository;
        this.accountService = accountService;
    }

    @Transactional
    public ModelMap entry(attendanceForInsert request, HttpSession session) {
        ModelMap rModelMap = new ModelMap();

        try {
            if (this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }
            BigInteger accountId = (BigInteger) session.getAttribute("accountId");

            Attendance attendance = attendanceRepository.findByAccountIdAndAttendanceDateAndUseYn(accountId, request.getAttendanceDate(),
                                                                                                  "Y");
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

            if (Objects.isNull(attendance)) {
                System.out.println("attendance not created");
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "attendance not created");
                rModelMap.addAttribute("attendance", Collections.emptyList());
                return rModelMap;
            }

            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("attendance", attendance);
        } catch (Exception e) {
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

            if (this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }

            Attendance attendance = attendanceRepository.findByAttendanceIdAndUseYn(request.getAttendanceId(), "Y");
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

        } catch (Exception e) {
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
            if (this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }

            Attendance attendance = attendanceRepository.findByAttendanceIdAndUseYn(request.getAttendanceId(), "Y");
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
        } catch (Exception e) {
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
        if (Objects.isNull(accountId)) {
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

        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");

        }

        return rModelMap;
    }

    public ModelMap getMyAttendanceCount(HttpSession session) {
        ModelMap rModelMap = new ModelMap();
        final int NOW = 0;
        try {
            if (this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }
            BigInteger accountId = (BigInteger) session.getAttribute("accountId");

            List<String> date4Weeks = CalendarUtil.mondayAndSundayof4Weeks(NOW);

            attendanceStatusListRequest request = new attendanceStatusListRequest();
            request.setStartDate(date4Weeks.get(0));
            request.setEndDate(date4Weeks.get(date4Weeks.size() - 1));
            request.setAccountId(accountId);

            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("myAttendance", attendanceRepository.findAllAttendanceStatusList(request));

        } catch (Exception e) {
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
            if (this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }
            if (!session.getAttribute("adminStatus").toString().equals("Y")) {
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "관리자가 아닌 사용자 계정 입니다.");

                return rModelMap;
            }

            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("attendanceList", attendanceRepository.findAllAttendanceStatusList(request));

        } catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
            rModelMap.addAttribute("attendanceList", Collections.emptyList());

        }
        return rModelMap;
    }

    @Transactional
    public ModelMap getWeeklyAttendanceStatus(AttendanceDto.attendanceExcelControllerRequest request, HttpServletRequest httpReq,
                                              HttpServletResponse httpRes,
                                              HttpSession session) {
        ModelMap rModelMap = new ModelMap();
        final int OFFLINE_POINT = 10;
        final int ONLINE_POINT = 0;
        try {
            String filePath = excelRootPath;
            String sheetName = "sheet1";
            String excelFileName = "sample.xlsx";
            int rowStart = 3;
            int rowEnd = 3;

            List<String> days = new ArrayList<String>();
            if (this.sessionIsNull(session)) {
                rModelMap = this.failSessionCheck(rModelMap);
                return rModelMap;
            }

            if (!session.getAttribute("adminStatus").toString().equals("Y")) {
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "관리자가 아닌 사용자 계정 입니다.");

                return rModelMap;
            }
            AttendanceDto.attendanceStatusListRequest atdSLR = new attendanceStatusListRequest();

            Map<String, Map<String, Object>> mapData = new HashMap<String, Map<String, Object>>();
            days = CalendarUtil.mondayAndSundayof4Weeks(request.getMoveNum());

            for (int i = 0; i < days.size(); i += 2) {
                atdSLR.setFirstDayOfWeek(days.get(i));
                atdSLR.setEndDayOfWeek(days.get(i + 1));

                System.out.printf("First : %s , End  : %s ", atdSLR.getFirstDayOfWeek(), atdSLR.getEndDayOfWeek());
                for (attendanceStatusListResponse row : attendanceRepository.findAllAttendanceStatusList(atdSLR)) { // N 주차 출석정보 추출
                    long nowPoint = row.getOfflineCount().longValue() * OFFLINE_POINT + row.getOnlineCount().longValue() * ONLINE_POINT;
                    List<Long> weekData = null;
                    if (i == 0) { // id 있는지 체크 후 생성 보다는 이게 더 속도빠름
                        weekData = new ArrayList<Long>();
                        weekData.add(nowPoint);
                        Map<String, Object> rowFormat = new HashMap<String, Object>();
                        rowFormat.put("accountId", row.getAccountId());
                        rowFormat.put("nickname", row.getNickname());
                        rowFormat.put("weekArr", weekData);
                        rowFormat.put("sum", weekData.get(i));
                        if (row.getAccountId() != null) {mapData.put(row.getAccountId().toString(), rowFormat);}
                    } else {
                        long sum = (long) mapData.get(row.getAccountId().toString()).get("sum") + nowPoint;
                        weekData = (List<Long>) mapData.get(row.getAccountId().toString()).get("weekArr");
                        weekData.add(nowPoint);
                        mapData.get(row.getAccountId().toString()).put("weekArr", weekData);
                        mapData.get(row.getAccountId().toString()).put("sum", sum);
                    }

                }

                // list 생성해서  [{id ,nickname , frist, second, thrid, forth ,총점(현재 점수)} , {id, nickname, points} ...]  수
            }
            // hashMap -> List 로 변경하는 과정
            //nickname : 0 , weekArr : 1, 2, 3, 4  , sum : 5
            List<List<String>> listData = new ArrayList<List<String>>();
            for (String strKey : mapData.keySet()) {
                Map<String, Object> mapRow = (HashMap<String, Object>) mapData.get(strKey);
                List<Long> weekArr = (List<Long>) mapRow.get("weekArr");
                List<String> toStringList = new ArrayList<String>();
                toStringList.add((String) mapRow.get("nickname"));
                for (int i = 0; i < weekArr.size(); i++) {
                    toStringList.add(Long.toString(weekArr.get(i)));
                }
                toStringList.add(Long.toString((Long) mapRow.get("sum")));

                listData.add(toStringList);
                //System.out.println(toStringList);
                //System.out.println(mapData.get(strKey));
            }

            // 전체 계산 후 정렬 시작(점수 오름차순) (stream?) ->> 정렬 후
            final int SUM_IDX = 5;
            Collections.sort(listData, new Comparator<List<String>>() {
                // Comparable 인터페이스를 구현하여 전달
                @Override
                public int compare(List<String> s1, List<String> s2) {
                    //return ((Integer.valueOf(s1.get(SUM_IDX)) - Integer.valueOf(s2.get(SUM_IDX))));
                    return s1.get(0).compareTo(s2.get(0));
                }
            });

            //listData
            // 순차적으로 excel 에 row 생성
            final CellCopyPolicy defaultCopyPolicy = new CellCopyPolicy();

            FileInputStream excelFile = new FileInputStream(filePath + excelFileName);
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
                XSSFSheet sheet = workbook.getSheet(sheetName);

                CellReference cellReference = new CellReference("C2");
                int rowN = cellReference.getRow();
                int colN = cellReference.getCol();

                Row row = sheet.getRow(rowN);
                if (row == null) {
                    row = sheet.createRow(rowN);
                }
                for (int i = 0; i < days.size(); i += 2) {
                    Cell cell = row.getCell(colN + i / 2);
                    if (cell == null) {
                        cell = row.createCell(colN + i / 2);
                    }
                    System.out.println(days.get(i).replace("-", ".") + "~" + days.get(i + 1).replace("-", "."));
                    cell.setCellValue(days.get(i).replace("-", ".") + "~" + days.get(i + 1).replace("-", "."));

                }

                //셀 양식 복사
                for (int i = 1; i < listData.size(); i++) {
                    int crn = (rowEnd - rowStart + 1) * i + rowStart - 1; //

                    sheet.copyRows(rowStart - 1, rowEnd - 1, crn, defaultCopyPolicy);
                }

                cellReference = new CellReference("A3");
                rowN = cellReference.getRow();
                colN = cellReference.getCol();
                //A3 , B3 , C3, D3, E3, F3, G3, H3 순서대로 값 insert
                for (int i = 0; i < listData.size(); i++) {
                    List<String> rowData = listData.get(i);
                    row = sheet.getRow(rowN + i);
                    if (row == null) {
                        row = sheet.createRow(rowN + i);
                    }

                    for (int j = 0; j <= rowData.size(); j++) {
                        Cell cell = row.getCell(colN + j);
                        if (cell == null) {
                            cell = row.createCell(colN + j);
                        }
//                        CellStyle cellStyle_Title = null;
//                        if(Integer.valueOf(rowData.get(rowData.size()-1 )) < 20) {
//                            cellStyle_Title = workbook.createCellStyle();
//                            cellStyle_Title.setBorderTop(BorderStyle.THIN); //테두리 위쪽
//                            cellStyle_Title.setBorderBottom(BorderStyle.THIN); //테두리 아래쪽
//                            cellStyle_Title.setBorderLeft(BorderStyle.THIN); //테두리 왼쪽
//                            cellStyle_Title.setBorderRight(BorderStyle.THIN); //테두리 오른쪽
//                            cellStyle_Title.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  // 배경색
//                            cellStyle_Title.setFillPattern(FillPatternType.SOLID_FOREGROUND);	//채우기 적용
//
//                        }

                        if (j == 0) {cell.setCellValue(i);} else {
                            if (StringUtills.canBeInt(rowData.get(j - 1))) {cell.setCellValue(Integer.valueOf(rowData.get(j - 1)));} else {
                                cell.setCellValue(rowData.get(j - 1));
                            }
                        }
//                        if(cellStyle_Title != null)
//                            cell.setCellStyle(cellStyle_Title);
                    }
                }

                //httpRes.setContentType("application/vnd.ms-excel");
                httpRes.setHeader("Content-Disposition", "attachment;filename=study.xlsx");

                XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
                workbook.write(httpRes.getOutputStream());

                workbook.close();
                excelFile.close();
                httpRes.getOutputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
                excelFile.close();

            }

            // insert 후 전송

            rModelMap.addAttribute("success", "ok");

            //workbook.write(httpRes.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
            rModelMap.addAttribute("attendanceList", Collections.emptyList());

        }
        return rModelMap;
    }

}
