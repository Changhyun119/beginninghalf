package com.toy.attendance.dev.model.account.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.toy.attendance.dev.model.account.dto.AccountDto;
import com.toy.attendance.dev.model.account.dto.AccountDto.AccountForInsert;
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
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value="카카오톡 로그인(인증) API", notes="")
    public ResponseEntity<ModelMap> oAuthKakaoLogin(@RequestParam String code) throws Exception{
        ModelMap rModelMap = accountService.getKakaoAccessToken(code);
        
        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value="signIn API", notes="로그인")
    public ResponseEntity<ModelMap> signIn(@RequestBody AccountForInsert request, HttpSession session, HttpServletResponse response) {
        ModelMap rModelMap = accountService.signIn(request, session, response);
        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/sign-out")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "SignOut API", notes = "로그아웃")
    public ResponseEntity<ModelMap> signOut(HttpSession session, HttpServletResponse response) throws Exception {
        ModelMap rModelMap = accountService.signOut(session, response);
        
        return ResponseEntity.ok(rModelMap);
    }
    
    @PostMapping("/session")
    @ApiOperation(value="session API", notes="세션 체크")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ModelMap> sessionCheck(HttpSession session,HttpServletRequest request) {
        ModelMap rModelMap = accountService.sessionCheck(request, session);

        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/permit-admin")
    @ApiOperation(value="관리자 권한 여부 설정 API", notes="")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ModelMap> accountAdminStatus(@RequestBody AccountDto.AccountForPermitAdmin request) {
        ModelMap rModelMap = accountService.setAccountAdminStatus(request);

        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/active-account")
    @ApiOperation(value="사용자 활성화 여부 설정 API", notes="")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ModelMap> ActivateAccountStatus(@RequestBody AccountDto.AccountForRemove request) {
        ModelMap rModelMap = accountService.setActivateAccountStatus(request);

        return ResponseEntity.ok(rModelMap);
    }

    @GetMapping("/active-all")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value="활성 사용자 전체 목록 API", notes="")
    public ResponseEntity<ModelMap> ActiveAccountAll() throws Exception{
        String useYn ="Y";
        ModelMap rModelMap = accountService.getAccountAll(useYn);
        
        return ResponseEntity.ok(rModelMap);
    }

    @GetMapping("/inactive-all")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value="비활성 사용자 전체 목록 API", notes="")
    public ResponseEntity<ModelMap> inActiveAccountAll() throws Exception{
        String useYn ="N";
        ModelMap rModelMap = accountService.getAccountAll(useYn);
        
        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/profile")
    @ApiOperation(value="사용자 개인정보 수정 API", notes="")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ModelMap> setProfile(@RequestBody AccountDto.AccountForUpdate request, HttpSession session) {
        ModelMap rModelMap = accountService.setProfile(request, session);

        return ResponseEntity.ok(rModelMap);
    }

    @PostMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public void test() {
       System.out.println(Long.valueOf("0"));
       Object a = null;
       Long b = (Long) a;
       System.out.println(b);
    }


}
