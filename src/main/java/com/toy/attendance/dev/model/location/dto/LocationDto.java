package com.toy.attendance.dev.model.location.dto;

import java.math.BigInteger;
import java.sql.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class LocationDto {
    private LocationDto() {
        throw new IllegalStateException("Static Dto");
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class locationForInsert {
        private String locationName;
        @Temporal(TemporalType.DATE)
        private Date attendanceDate;
        private String official = "N";
        
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class locationForUpdate {
        private BigInteger locationId;
        private String locationName;
        private String official;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class locationForDelete {
        private BigInteger locationId;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class locationListRequest {
        private Date attendanceDate;
    }
    
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class dsLocation {
        private String locationName;
        private String official;
        private String useYn;
   
        public dsLocation (
            String locationName,
            String official,
            String useYn
        ) {
            this.locationName = locationName;
            this.official = official;
            this.useYn = useYn;
        }
    }
}
