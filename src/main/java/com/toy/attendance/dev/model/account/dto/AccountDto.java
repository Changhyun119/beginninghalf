package com.toy.attendance.dev.model.account.dto;

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
    public static class accountForInsert {
        //cnstSerno , floor, geom, dong, pourDate , pourTemperature, specialManageCheck ,
        private String email; // insert Crack and return <-crackId;
        private String nickname;
        private Long kakaoId;
        private String useYn = "Y";
    }

}
