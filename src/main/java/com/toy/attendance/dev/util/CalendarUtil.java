package com.toy.attendance.dev.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarUtil {

    final static Calendar defaultDay = Calendar.getInstance();
    final static Calendar cal = Calendar.getInstance();
    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static String getDefaultDay(int moveNum) {
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        defaultDay.set(2022, Calendar.NOVEMBER, 14); // 기준일 2022-11-14로 초기값 지정

        long diffSec = (today.getTimeInMillis() - defaultDay.getTimeInMillis()) / 1000;
        long diffDays = diffSec / (24 * 60 * 60); //일자수 차이
        int diff4Weeks = (Long.valueOf(diffDays).intValue() / 28) + moveNum;

        defaultDay.add(Calendar.DATE, diff4Weeks * 28);

        return sdf.format(defaultDay.getTime());
    }

    public static int lastDayofMonth(String yearAndMonth) throws ParseException {
        yearAndMonth += "-01";
        cal.setTime(sdf.parse(yearAndMonth));

        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static List<String> mondayAndSundayof4Weeks(int moveNum) throws ParseException {
        List<String> getdays = new ArrayList<>();
        final int WEEK_UNIT = 4;
        cal.setTime(sdf.parse(CalendarUtil.getDefaultDay(moveNum)));

        for (int i = 0; i < WEEK_UNIT; i++) {
            cal.set(Calendar.DAY_OF_WEEK, 2);
            getdays.add(sdf.format(cal.getTime()));

            cal.add(Calendar.DATE, 7);

            cal.set(Calendar.DAY_OF_WEEK, 1);
            getdays.add(sdf.format(cal.getTime()));

        }

        return getdays;
    }

    public static List<String> mondayAndSundayofMonth(String dateStr) throws ParseException {
        String endDayofMonth = dateStr + "-" + lastDayofMonth(dateStr);
        dateStr += "-01";

        List<String> getdays = new ArrayList<>();
        String getMonth = dateStr.split("-")[1];
        cal.setTime(sdf.parse(dateStr));

        while (Integer.parseInt(getMonth) == cal.get(Calendar.MONTH) + 1) {
            cal.set(Calendar.DAY_OF_WEEK, 2);
            getdays.add(sdf.format(cal.getTime()));

            cal.add(Calendar.DATE, 7);
            cal.set(Calendar.DAY_OF_WEEK, 1);
            getdays.add(sdf.format(cal.getTime()));
        }

        // 첫날보다 list 의 값이 작을경우 list 첫번째 요소를 첫날로 교체 (처음 시작이 1일로 시작할필요없이 한주 단위를 원해서 취소)
        // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Date checkStart = dateFormat.parse(dateStr);
        // Date fristArray = dateFormat.parse(getdays.get(0));
        // if(checkStart.compareTo(fristArray) > 0) 
        //     getdays.set(0,dateStr);

        if (getdays.size() > 10) { // 5주차는 고정하기위해 10보다크면 제거 6주차의 경우 배제
            int length = getdays.size();
            for (int i = 10; i < length; i++) {
                getdays.remove(getdays.size() - 1);
            }
        }
        getdays.set(9, endDayofMonth);

        return getdays;
    }

    public static String sundayofDay(String dateStr) throws ParseException {
        cal.setTime(sdf.parse(dateStr));

        return sdf.format(cal.getTime());
    }

}
