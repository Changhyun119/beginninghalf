package com.toy.attendance.dev.model.account.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.attendance.dev.model.account.dto.AccountDto;
import com.toy.attendance.dev.model.account.dto.AccountDto.AccountForInsert;
import com.toy.attendance.dev.model.account.entity.Account;
import com.toy.attendance.dev.model.account.repository.AccountRepository;

@Transactional
@Service
public class AccountService {
    final String REST_API_KEY = "683a25bcc3f527d02f9db7c483c99196";
    final String REDIRECT_URI = "http://3.37.185.41/app/kakao/oauth";

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

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

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
            Long id = (Long) map.get("id") ;
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
            System.out.println(session);

            AccountDto.SignInResponse signInResponse = new AccountDto.SignInResponse();
            signInResponse.setNickname(account.getNickname());
            signInResponse.setAccountId(account.getAccountId());
            signInResponse.setKakaoId(account.getKakaoId());
            
            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("attendanceUser", signInResponse);

             // 자동로그인
             if (request.getIsAutoLogin()) {
                // 쿠키정보 저장
                Cookie cookie = new Cookie("autoLoginAttendance", String.valueOf((Long) account.getKakaoId()));
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
        Long kakaoId = null;
        String nickname = "";
        Long accountId = null;
        String success = "fail";

        // 리턴 ModelMap
        ModelMap rModelMap = new ModelMap();

        // 쿠키 정보
        Cookie[] cookies = hRequest.getCookies();
        if (Objects.nonNull(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("autoLoginAttendance")) {
                    kakaoId = Long.valueOf(cookie.getValue());
                }
            }
        }

        // 세션 정보 존재하는 경우
        if (Objects.nonNull(session.getAttribute("kakaoId"))) {
            kakaoId = Long.valueOf(session.getAttribute("kakaoId").toString());
            nickname = session.getAttribute("nickname").toString();
            accountId =  Long.valueOf(session.getAttribute("accountId").toString());
            success = "ok";
        }
        // 쿠키 정보 존재하는 경우
        else if (Objects.isNull(kakaoId)) {
            Account account = accountRepository.findByKakaoIdAndUseYn(kakaoId, "Y");
            if (Objects.nonNull(account)) {
                nickname = account.getNickname();
                kakaoId = account.getKakaoId();
                accountId =account.getAccountId();
                success = "ok";

                session.setAttribute("accountId", accountId);
                session.setAttribute("nickname", nickname);
                session.setAttribute("kakaoId", kakaoId);

                
            }
        }

        AccountDto.SignInResponse signInResponse = new AccountDto.SignInResponse();
        signInResponse.setNickname(nickname);
        signInResponse.setAccountId( accountId );
        signInResponse.setKakaoId( kakaoId );

        // 리턴 정보
        rModelMap.addAttribute("success", success);
        rModelMap.addAttribute("attendanceUser", signInResponse);

        return rModelMap;
    }

   
}
