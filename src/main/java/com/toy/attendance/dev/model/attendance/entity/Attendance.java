package com.toy.attendance.dev.model.attendance.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import com.toy.attendance.dev.model.attendance.dto.AttendanceDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "attendance")
public class Attendance implements Serializable {
    private static final Long serialVersionUID = 1L; 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 테이블 컬럼 -> set default() 로 sequence 가 설정되어있어야함
    @Column(name = "attendance_id")
    private BigInteger attendanceId;
    
    @NonNull
    @Column(name = "account_id")
    private BigInteger accountId;

    @Column(name = "attendance_date")
    private Date attendanceDate;

    @Column(name = "reg_date")
    @CreationTimestamp
    private LocalDateTime regDate;

    @Column(name = "updt_date")
    @UpdateTimestamp
    private LocalDateTime updtDate;

    @Column(name = "use_yn")
    private String useYn;

    @Column(name = "meal_status")
    private String mealStatus;

    @Setter
    @Column(name = "location_id")
    private BigInteger locationId;

    @PrePersist
    public void prePersist() {
        this.useYn = this.useYn == null ? "Y" : this.useYn;
    }
   
    @Builder
    public Attendance(
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

    public void updateAttendance(AttendanceDto.dsAttendance dto) {
        this.updtDate = LocalDateTime.now();
        if (Objects.nonNull(dto.getAttendanceDate())) this.attendanceDate = dto.getAttendanceDate();
        if (Objects.nonNull(dto.getMealStatus())) this.mealStatus = dto.getMealStatus();
        if (Objects.nonNull(dto.getLocationId())) this.locationId = dto.getLocationId();
        if (Objects.nonNull(dto.getUseYn())) this.useYn = dto.getUseYn();
    }
}
