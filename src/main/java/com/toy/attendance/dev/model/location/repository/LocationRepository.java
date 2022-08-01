package com.toy.attendance.dev.model.location.repository;



import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.toy.attendance.dev.model.location.dto.LocationDto.locationForUpdate;
import com.toy.attendance.dev.model.location.dto.LocationDto.locationListRequest;
import com.toy.attendance.dev.model.location.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long>, LocationRepositoryCustom{

    
    Location findByLocationNameAndAttendanceDateAndUseYn(String locationName, Date attendanceDate, String useYn);

    Location findByLocationIdAndUseYn(BigInteger locationId, String useYn);

    List<Location> findByAttendanceDateAndUseYn(Date attendanceDate, String useYn);

  
    
}
