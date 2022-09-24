package com.toy.attendance.dev.model.attendance.dto;

import java.math.BigInteger;
import java.sql.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class AttendanceDto {
    private AttendanceDto() {
        throw new IllegalStateException("Static Dto");
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class attendanceForInsert {
        @Temporal(TemporalType.DATE)
        private Date attendanceDate;
        private String mealStatus = "N";
        private BigInteger locationId;
        
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class attendanceForUpdate {
        private BigInteger attendanceId;
        @Temporal(TemporalType.DATE)
        private Date attendanceDate;
        private String mealStatus = "N";
        private BigInteger locationId;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class attendanceForDelete {
        private BigInteger attendanceId;
    }
    
    @Getter
    @ToString
    @NoArgsConstructor
    public static class attendanceListResponse {
        private BigInteger attendanceId;
        private BigInteger accountId;
        private String nickname;
        private Date attendanceDate;
        private Date regDate;
        private Date updtDate;
        private String useYn;
        private String mealStatus;
        private BigInteger locationId;
        private String locationName;
       
        public attendanceListResponse (
            BigInteger attendanceId,
            BigInteger accountId,
            String nickname,
            Date attendanceDate,
            Date regDate,
            Date updtDate,
            String useYn,
            String mealStatus,
            BigInteger locationId,
            String locationName
        ) {
            this.attendanceId = attendanceId;
            this.accountId = accountId;
            this.nickname = nickname;
            this.attendanceDate = attendanceDate;
            this.regDate = regDate;
            this.updtDate = updtDate;
            this.useYn = useYn;
            this.mealStatus = mealStatus;
            this.locationId = locationId;
            this.locationName = locationName;
        }
       
    }

    @Getter
    @ToString
    @NoArgsConstructor
    public static class attendanceStatusListResponse {
        private BigInteger accountId;
        private String nickname;
        private String regDate;
        private BigInteger attendanceAccount;
        private BigInteger offlineCount;
        private BigInteger onlineCount;

        public attendanceStatusListResponse (
            BigInteger accountId,
            String nickname,
            String regDate,
            BigInteger attendanceAccount,
            BigInteger offlineCount,
            BigInteger onlineCount
            ) {
            this.accountId = accountId;
            this.nickname = nickname;
            this.regDate = regDate;
            this.attendanceAccount = attendanceAccount;
            this.offlineCount = offlineCount;
            this.onlineCount = onlineCount;
        }
       
    }
    
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class attendanceListRequestByYearAndMonth { //Controller 에서 이용하는 DTO , (list, attendance-status-on-month)
        private String year; // 월별
        private String month; // 월별
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class attendanceListRequestByAttendanceDate { //Controller 에서 이용하는 DTO , (list-on-date)
        private Date attendanceDate; // 일별
     
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class attendanceListRequest { // service 단에서 이용할 DTO , 월별 혹은 특정일로 서치 가능하게 파라미터 종류 구분됨
        private Date attendanceDate; // 일별
        private String year; // 월별
        private String month; // 월별
    }


    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class attendanceStatusListRequest { // service 단에서 이용할 DTO , 월별 혹은 특정일로 서치 가능하게 파라미터 종류 구분됨
        private String year; // 월별
        private String month; // 월별
        private BigInteger accountId; // 나의 출석 현황 보기시 계정정보 필요
    }
    


    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class dsAttendance {
        private BigInteger accountId;
        private Date attendanceDate;
        private String mealStatus;
        private BigInteger locationId;
        private String useYn;
   
        public dsAttendance (
            BigInteger accountId,
            Date attendanceDate,
            String mealStatus,
            BigInteger locationId,
            String useYn
        ) {
            this.accountId = accountId;
            this.attendanceDate = attendanceDate;
            this.mealStatus = mealStatus;
            this.locationId = locationId;
            this.useYn = useYn;
        }
    }

    

}
