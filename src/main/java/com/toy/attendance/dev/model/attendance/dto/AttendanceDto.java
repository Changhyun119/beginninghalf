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
    @Setter
    @ToString
    @NoArgsConstructor
    public static class attendanceListRequest {
        private Date attendanceDate;
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
