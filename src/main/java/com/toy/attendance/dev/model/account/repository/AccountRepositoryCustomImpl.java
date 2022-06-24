package com.toy.attendance.dev.model.account.repository;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.toy.attendance.dev.model.account.entity.Account;

public class AccountRepositoryCustomImpl extends QuerydslRepositorySupport implements AccountRepositoryCustom{

    public AccountRepositoryCustomImpl() {
        super(Account.class);
        //TODO Auto-generated constructor stub
    }
    
}
