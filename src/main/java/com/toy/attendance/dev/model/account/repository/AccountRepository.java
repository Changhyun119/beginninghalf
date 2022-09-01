package com.toy.attendance.dev.model.account.repository;



import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.toy.attendance.dev.model.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryCustom{

    List<Account> findByUseYn(String useYn);

    Account findByAccountIdAndUseYn(BigInteger accountId, String useYn);

    Account findByAccountId(BigInteger accountId);
    
    Account findByKakaoIdAndUseYn(BigInteger kakaoId, String useYn);
    
}
