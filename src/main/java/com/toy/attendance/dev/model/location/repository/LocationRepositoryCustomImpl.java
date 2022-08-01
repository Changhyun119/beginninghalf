package com.toy.attendance.dev.model.location.repository;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.toy.attendance.dev.model.location.entity.Location;

public class LocationRepositoryCustomImpl extends QuerydslRepositorySupport implements LocationRepositoryCustom{

    public LocationRepositoryCustomImpl() {
        super(Location.class);
        //TODO Auto-generated constructor stub
    }
    
}
