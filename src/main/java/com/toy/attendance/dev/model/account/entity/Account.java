package com.toy.attendance.dev.model.account.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "account")
public class Account implements Serializable {
    private static final long serialVersionUID = 1L; 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 테이블 컬럼 -> set default() 로 sequence 가 설정되어있어야함
    @Column(name = "account_id")
    private BigInteger accountId;

    @Column(name = "email")
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "kakao_id")
    private BigInteger kakaoId;

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
    public Account(
        BigInteger accountId,
        String email,
        String nickname,
        BigInteger kakaoId
    ) {
        this.accountId = accountId;
        this.email = email;
        this.nickname = nickname;
        this.kakaoId = kakaoId;
    }
}
