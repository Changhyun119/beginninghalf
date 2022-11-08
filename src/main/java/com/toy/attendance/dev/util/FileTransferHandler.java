package com.toy.attendance.dev.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileTransferHandler {
    public static void fileTransfer(HttpServletRequest request, HttpServletResponse response, String fileName) throws Exception {
            InputStream in = null;
            OutputStream os = null;
            File file = null;
            boolean skip = false;
            String client = "";
            
            //파일을 읽어 스트림에 담기  
            try{
                file = new File(fileName);
                in = new FileInputStream(file);
            } catch (FileNotFoundException fe) {
                skip = true;
            }
            
            client = request.getHeader("User-Agent");
            
            //파일 다운로드 헤더 지정 
            //response.reset();
            
            response.setContentLengthLong(file.length());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Length", "" + file.length());
            if (!skip) {
                // IE
                if (client.indexOf("MSIE") != -1) {
                    response.setHeader("ContentDisposition", "attachment; filename=\""
                            + java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
                    // IE 11 이상.
                } else if (client.indexOf("Trident") != -1) {
                    response.setHeader("ContentDisposition", "attachment; filename=\""
                            + java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
                } else {
                    // 한글 파일명 처리
                    response.setHeader("ContentDisposition",
                            "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859_1") + "\"");
                    response.setHeader("Content-Type", "application/octet-stream; charset=utf-8");
                }
                os = response.getOutputStream();
                byte b[] = new byte[(int) file.length()];
                int leng = 0;
                while ((leng = in.read(b)) > 0) {
                    os.write(b, 0, leng);
                }
            } else {
                response.setContentType("text/html;charset=UTF-8");
                System.out.println("<script language='javascript'>alert('파일을 찾을 수 없습니다');history.back();</script>");
            }
            System.out.println("FileTransFer-------------------------------------- end!!!!!!!!!!!!!");
            in.close();
            os.close();
    }

    public static void fileTransfer(HttpServletRequest request, HttpServletResponse response, String fileName,FileInputStream in) throws Exception {
        System.out.println(in.toString()); // 파일 inputStream stream Closed 에러 체크 필요
        OutputStream os = null;
        File file = new File(fileName);
        boolean skip = false;
        String client = "";
        
        //파일을 읽어 스트림에 담기  
       
        client = request.getHeader("User-Agent");
        
        //파일 다운로드 헤더 지정 
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Description", "Style Download");
        
        if (!skip) {
            // IE
            if (client.indexOf("MSIE") != -1) {
                response.setHeader("Content-Disposition", "attachment; filename=\""
                        + java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
                // IE 11 이상.
            } else if (client.indexOf("Trident") != -1) {
                response.setHeader("Content-Disposition", "attachment; filename=\""
                        + java.net.URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
            } else {
                // 한글 파일명 처리
                response.setHeader("Content-Disposition",
                        "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859_1") + "\"");
                response.setHeader("Content-Type", "application/octet-stream; charset=utf-8");
            }
            response.setHeader("Content-Length", "" + file.length());
            os = response.getOutputStream();
            byte b[] = new byte[(int) file.length()];
            int leng = 0;
            while ((leng = in.read(b)) > 0) {
                os.write(b, 0, leng);
            }
        } else {
            response.setContentType("text/html;charset=UTF-8");
            System.out.println("<script language='javascript'>alert('파일을 찾을 수 없습니다');history.back();</script>");
        }
        in.close();
        os.close();
}
}
