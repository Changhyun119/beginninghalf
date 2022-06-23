package com.toy.attendance.dev.model.account.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.toy.attendance.dev.model.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryCustom{

    Account findByKakaoIdAndUseYn(Long kakaoId, String useYn);
    
}
