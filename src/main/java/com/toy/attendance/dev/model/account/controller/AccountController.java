package com.toy.attendance.dev.model.account.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toy.attendance.dev.model.account.service.AccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Api(tags = "#1.Account Controller", value = "/", description = "")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;

    }
    @GetMapping("/app/kakao/oauth")
    public ResponseEntity<ModelMap> oAuthKakaoLogin(@RequestParam String code) throws Exception{
        ModelMap rModelMap = accountService.getKakaoAccessToken(code);
        
        return ResponseEntity.ok(rModelMap);
    }
}
