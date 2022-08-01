package com.toy.attendance.dev.model.account.dto;

import java.math.BigInteger;
import java.sql.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class AccountDto {
    private AccountDto() {
        throw new IllegalStateException("Static Dto");
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class AccountForInsert {
        
        private String email;
        private String nickname;
        private BigInteger kakaoId;
        private Boolean isAutoLogin = false;
    }

    @Getter
    @Setter
    public static class SignInResponse {
        private String nickname;
        private BigInteger accountId;
        private BigInteger kakaoId;
    }

}
