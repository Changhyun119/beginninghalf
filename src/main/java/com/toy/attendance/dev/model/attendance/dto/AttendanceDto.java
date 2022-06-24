package com.toy.attendance.dev.model.attendance.dto;

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
        
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class attendanceForUpdate {
        private Long attendanceId;
        @Temporal(TemporalType.DATE)
        private Date attendanceDate;
        private String mealStatus = "N";
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class attendanceForDelete {
        private Long attendanceId;
    }


    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class dsAttendance {
        private Long accountId;
        private Date attendanceDate;
        private String mealStatus;
   
        public dsAttendance (
            Long accountId,
            Date attendanceDate,
            String mealStatus
        ) {
            this.accountId = accountId;
            this.attendanceDate = attendanceDate;
            this.mealStatus = mealStatus;
        }
    }

    

}
