package com.toy.attendance.dev.model.location.entity;

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

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import com.toy.attendance.dev.model.location.dto.LocationDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "location")
public class Location implements Serializable {
    private static final long serialVersionUID = 1L; 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 테이블 컬럼 -> set default() 로 sequence 가 설정되어있어야함
    @Column(name = "location_id")
    private BigInteger locationId;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "account_id")
    private BigInteger accountId;

    @Column(name = "official")
    private String official;

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

    @PrePersist
    public void prePersist() {
        this.useYn = this.useYn == null ? "Y" : this.useYn;
    }

    @Builder
    public Location(
        BigInteger locationId,
        String locationName,
        Date attendanceDate,
        BigInteger accountId,
        String official,
        String useYn
    ) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.attendanceDate = attendanceDate;
        this.accountId = accountId;
        this.official = official;
        this.useYn = useYn;
    }

    
    public void updateLocation(LocationDto.dsLocation dto) {
        this.updtDate = LocalDateTime.now();
        if (Objects.nonNull(dto.getLocationName())) this.locationName = dto.getLocationName();
        if (Objects.nonNull(dto.getOfficial())) this.official = dto.getOfficial();
        if (Objects.nonNull(dto.getUseYn())) this.useYn = dto.getUseYn();
    }
}
