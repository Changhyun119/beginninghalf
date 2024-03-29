package com.toy.attendance.dev.model.account.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.attendance.dev.model.account.dto.AccountDto;
import com.toy.attendance.dev.model.account.dto.AccountDto.AccountForInsert;
import com.toy.attendance.dev.model.account.dto.AccountDto.AccountForPermitAdmin;
import com.toy.attendance.dev.model.account.dto.AccountDto.AccountForRemove;
import com.toy.attendance.dev.model.account.dto.AccountDto.AccountForUpdate;
import com.toy.attendance.dev.model.account.entity.Account;
import com.toy.attendance.dev.model.account.repository.AccountRepository;

@Transactional
@Service
public class AccountService {

    @Value("${REDIRECT_URI}")
    private String REDIRECT_URI;

    @Value("${REST_API_KEY}")
    private String REST_API_KEY;


    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository= accountRepository;
    }

    @SuppressWarnings("unchecked")
    public ModelMap getKakaoAccessToken(String code) throws Exception {
        ModelMap rModelMap = new ModelMap();
        String accessToken = "";
        String refreshToken = "";
        Map<String, String> map = new HashMap<String,String>();
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id="+ REST_API_KEY); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri="+ REDIRECT_URI); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();
            System.out.println(sb.toString());

           
            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            String resMeg =    conn.getResponseMessage();
           
            System.out.println("responseCode : " + responseCode);
            System.out.println("responseMessage : " + resMeg );
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            ObjectMapper mapper = new ObjectMapper();
            if(mapper.readValue(result, Map.class) instanceof Map) {
             map = mapper.readValue(result, Map.class);
            }

            accessToken = map.get("access_token");
            refreshToken = map.get("refresh_token");

            System.out.println("access_token : " + accessToken);
            System.out.println("refresh_token : " + refreshToken);

            br.close();
            bw.close();
            rModelMap = this.getKakaoUser(accessToken);
            if(!(rModelMap.getAttribute("success").equals("ok")) ) {
                throw new Exception("Fail Kakao API Login");
            }
            // rModelMap = this.signIn(rModelMap);

        } catch (IOException e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");

        }
        System.out.println(REDIRECT_URI);
        return rModelMap;
    }
    
    @SuppressWarnings("unchecked")
    public ModelMap getKakaoUser(String accessToken) {
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        ModelMap rModelMap = new ModelMap();
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
     
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken); //전송할 header 작성, access_token전송
     
            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
     
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line = "";
            String result = "";
     
            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);
     
            //Gson 라이브러리로 JSON파싱
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(result, Map.class);
            System.out.println("map : " + map);
            BigInteger id = (BigInteger) map.get("id") ;
            String nickname =  (String) ((Map<String,Object>) map.get("properties")).get("nickname");
            boolean hasEmail = (boolean) ((Map<String,Object>) map.get("kakao_account")).get("has_email");
            String email = "";
            if(hasEmail){
                email = (String) ((Map<String,Object>) map.get("kakao_account")).get("email");
            }
     
            System.out.println("id : " + id);
            System.out.println("email : " + email);
            AccountForInsert dto = new AccountForInsert();
            dto.setKakaoId(id);
            dto.setNickname(nickname);
            dto.setEmail(email);
            br.close();

            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("kakaoUserInfo", dto);
            
            
     
            } catch (IOException e) {
                 e.printStackTrace();
                 rModelMap.addAttribute("success", "fail");
                 rModelMap.addAttribute("reason", "unKnown cause");
            }
            return rModelMap;
    }
    
    public ModelMap signIn(AccountForInsert request, HttpSession session, HttpServletResponse response) {
        ModelMap rModelMap = new ModelMap();

        try {
            AccountForInsert dto = request;
            Account account = accountRepository.findByKakaoIdAndUseYn(dto.getKakaoId(),"Y");
            if( Objects.isNull(account) ) {
                account = this.signUp(dto);
                if(Objects.isNull(account) ) {
                    rModelMap.addAttribute("success", "fail");
                    rModelMap.addAttribute("reason", "계정이 생성되지 않음");
                    return rModelMap; 
                }
            }// 위 조건에 따라 account 는 null이 아님

            session.setAttribute("nickname", account.getNickname());
            session.setAttribute("accountId", account.getAccountId());
            session.setAttribute("kakaoId", account.getKakaoId());
            session.setAttribute("adminStatus", account.getAdminStatus());
            System.out.println(session);

            AccountDto.SignInResponse signInResponse = new AccountDto.SignInResponse();
            signInResponse.setNickname(account.getNickname());
            signInResponse.setAccountId(account.getAccountId());
            signInResponse.setKakaoId(account.getKakaoId());
            signInResponse.setAdminStatus(account.getAdminStatus());
            
            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("attendanceUser", signInResponse);

             // 자동로그인
             if (request.getIsAutoLogin()) {
                // 쿠키정보 저장
                Cookie cookie = new Cookie("autoLoginAttendance", String.valueOf((BigInteger) account.getKakaoId()));
                cookie.setMaxAge(7 * 24 * 60 * 60);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
       }
       return rModelMap;
    }
    
    public Account signUp(AccountForInsert dto) {
        try {
            Account checkAccount = accountRepository.findByKakaoIdAndUseYn(dto.getKakaoId(),"N");

            if ( Objects.nonNull(checkAccount) ) return null; // 이미 존재했던 사용자 , 회원가입 거절

            System.out.println("Sign Up !");
            System.out.println(dto);
            Account account = accountRepository.save(
                Account.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .kakaoId(dto.getKakaoId())
                .build()
            );
            
            return account;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
       }
       
         
    }

    public ModelMap signOut(HttpSession session, HttpServletResponse response) {
        // 리턴 ModelMap
        ModelMap rModelMap = new ModelMap();

        try {
            // 세션 초기화
            rModelMap.addAttribute("success", "ok");
            session.invalidate();
            
            // 쿠키 초기화
            Cookie cookie = new Cookie("autoLoginAttendance", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

        } catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
        }

        return rModelMap;
    }


    public ModelMap sessionCheck(HttpServletRequest hRequest, HttpSession session) {
        // 변수 설정
        BigInteger kakaoId = null;
        String nickname = "";
        BigInteger accountId = null;
        String adminStatus ="";
        String success = "fail";

        // 리턴 ModelMap
        ModelMap rModelMap = new ModelMap();

        // 쿠키 정보
        Cookie[] cookies = hRequest.getCookies();
        if (Objects.nonNull(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("autoLoginAttendance")) {
                    kakaoId = new BigInteger(cookie.getValue());
                }
            }
        }
        System.out.println("세션값 : ");
        System.out.println(session.getAttribute("kakaoId"));
        System.out.println("쿠키값 : ");
        System.out.println(kakaoId);
        // 세션 정보 존재하는 경우
        if (Objects.nonNull(session.getAttribute("kakaoId"))) {
            System.out.println("세션정보 확인됨");
            kakaoId = (BigInteger) session.getAttribute("kakaoId");
        }
        // 쿠키 정보 존재하는 경우
        else if (Objects.nonNull(kakaoId)) {
            System.out.println("쿠키정보 확인됨");
        }

        Account account = accountRepository.findByKakaoIdAndUseYn((BigInteger) session.getAttribute("kakaoId"), "Y");
            if (Objects.nonNull(account)) {
                nickname = account.getNickname();
                kakaoId = account.getKakaoId();
                accountId =account.getAccountId();
                adminStatus = account.getAdminStatus();
                
                success = "ok";

                session.setAttribute("accountId", accountId);
                session.setAttribute("nickname", nickname);
                session.setAttribute("kakaoId", kakaoId);
                session.setAttribute("adminStatus", adminStatus);

                
            }

        AccountDto.SignInResponse signInResponse = new AccountDto.SignInResponse();
        signInResponse.setNickname(nickname);
        signInResponse.setAccountId( accountId );
        signInResponse.setKakaoId( kakaoId );
        signInResponse.setAdminStatus( adminStatus) ;

        // 리턴 정보
        rModelMap.addAttribute("success", success);
        rModelMap.addAttribute("attendanceUser", signInResponse);

        return rModelMap;
    }

    public ModelMap getAccountAll(String useYn) {
         ModelMap rModelMap = new ModelMap();

         try {
            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("accountInfo", accountRepository.findByUseYn(useYn));

         } catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
         }
 
         return rModelMap;
    }
    @Transactional
    public ModelMap setAccountAdminStatus(AccountForPermitAdmin request) {
        ModelMap rModelMap = new ModelMap();

        try {
            Account account = accountRepository.findByAccountIdAndUseYn(request.getAccountId(),"Y");

            if(Objects.isNull(account)) {
                    rModelMap.addAttribute("success", "fail");
                    rModelMap.addAttribute("reason", "사용자 미존재");
                    return rModelMap;
            }

            AccountDto.dsAccount accountDto = new AccountDto.dsAccount();
            
            if (account.getAdminStatus().equals("Y"))
                accountDto.setAdminStatus("N");
            else 
                accountDto.setAdminStatus("Y");

            account.updateAccount(accountDto);
            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("account",account);
           

        } catch (Exception e) {
           e.printStackTrace();
           rModelMap.addAttribute("success", "fail");
           rModelMap.addAttribute("reason", "unKnown cause");
        }

        return rModelMap;
    }

    @Transactional
    public ModelMap setActivateAccountStatus(AccountForRemove request) {
        ModelMap rModelMap = new ModelMap();

        try {
            Account account = accountRepository.findByAccountId(request.getAccountId());

            if(Objects.isNull(account)) {
                    rModelMap.addAttribute("success", "fail");
                    rModelMap.addAttribute("reason", "사용자 미존재");
                    return rModelMap;
            }

            AccountDto.dsAccount accountDto = new AccountDto.dsAccount();
            if (account.getUseYn().equals("Y"))
                accountDto.setUseYn("N");
            else 
                accountDto.setUseYn("Y");
            account.updateAccount(accountDto);

            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("account",account);
           

        } catch (Exception e) {
           e.printStackTrace();
           rModelMap.addAttribute("success", "fail");
           rModelMap.addAttribute("reason", "unKnown cause");
        }

        return rModelMap;
    }

    @Transactional
    public ModelMap setProfile(AccountForUpdate request, HttpSession session) {
        ModelMap rModelMap = new ModelMap();

        try {
            Account account = accountRepository.findByAccountId(request.getAccountId());

            if(Objects.isNull(account)) {
                    rModelMap.addAttribute("success", "fail");
                    rModelMap.addAttribute("reason", "사용자 미존재");
                    return rModelMap;
            }

            if(!(account.getAccountId().equals(session.getAttribute("accountId")))) {
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "사용자 본인 아님");
                return rModelMap;
            }

            if(request.getNickname().length() > 20 ) {
                rModelMap.addAttribute("success", "fail");
                rModelMap.addAttribute("reason", "닉네임 길이제한");
                return rModelMap;
            }
             

            AccountDto.dsAccount accountDto = new AccountDto.dsAccount();
            
            accountDto.setNickname(request.getNickname());
            
            account.updateAccount(accountDto);

            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("account",account);
           

        } catch (Exception e) {
           e.printStackTrace();
           rModelMap.addAttribute("success", "fail");
           rModelMap.addAttribute("reason", "unKnown cause");
        }

        return rModelMap;
    }

   
   
}
