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

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.attendance.dev.model.account.dto.AccountDto;
import com.toy.attendance.dev.model.account.entity.Account;
import com.toy.attendance.dev.model.account.repository.AccountRepository;

@Service
public class AccountService {
    final String REST_API_KEY = "683a25bcc3f527d02f9db7c483c99196";
    final String REDIRECT_URI = "http://localhost:11945/app/kakao/oauth";

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
            rModelMap = this.signIn(rModelMap);

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
            AccountDto.accountForInsert dto = new AccountDto.accountForInsert();
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
    
    public ModelMap signIn(ModelMap getModelMap) {
        ModelMap rModelMap = new ModelMap();

        try {
            AccountDto.accountForInsert dto = (AccountDto.accountForInsert) getModelMap.getAttribute("kakaoUserInfo");
            Account account = accountRepository.findByKakaoIdAndUseYn(dto.getKakaoId(),"Y");
            if( Objects.isNull(account) ) {
                account = this.signUp(dto);
                if(Objects.isNull(account) ) {
                    rModelMap.addAttribute("success", "fail");
                    rModelMap.addAttribute("reason", "계정이 생성되지 않음");
                    return rModelMap; 
                }
            }

            // 위 조건에 따라 account 는 null이 아님
            
            rModelMap.addAttribute("success", "ok");
            rModelMap.addAttribute("attendanceUser", account);
        }
        catch (Exception e) {
            e.printStackTrace();
            rModelMap.addAttribute("success", "fail");
            rModelMap.addAttribute("reason", "unKnown cause");
       }
       return rModelMap;
    }

    public Account signUp(AccountDto.accountForInsert dto) {
        try {
            System.out.println("Sign Up !");
            System.out.println(dto);
            Account account = accountRepository.save(
                Account.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .kakaoId(dto.getKakaoId())
                .useYn(dto.getUseYn())
                .build()
            );
            
            return account;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
       }
       
         
    }
}
