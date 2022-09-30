package com.toy.attendance.dev.model.attendance.repository;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.query.Param;
import org.hibernate.annotations.Parameter;
import org.qlrm.mapper.JpaResultMapper;
import com.toy.attendance.dev.model.attendance.dto.AttendanceDto;
import com.toy.attendance.dev.model.attendance.entity.Attendance;

public class AttendanceRepositoryCustomImpl extends QuerydslRepositorySupport implements AttendanceRepositoryCustom{

    public AttendanceRepositoryCustomImpl() {
        super(Attendance.class);
        //TODO Auto-generated constructor stub
    }
    
    @Override
    public List<AttendanceDto.attendanceListResponse> findAllList(AttendanceDto.attendanceListRequest request) throws Exception {

        EntityManager em = getEntityManager();

        // Native Query
        String strSQL = findAllListNativeQuery(request);
        System.out.println(strSQL);
        // Native Query 실행
        Query nativeQuery = em.createNativeQuery(strSQL);
        if (Objects.nonNull( request.getAttendanceDate() ) ) {
            nativeQuery.setParameter("attendanceDate", request.getAttendanceDate());
        }
        if (Objects.nonNull( request.getYear() ) && Objects.nonNull( request.getMonth() ) ) {
            nativeQuery.setParameter("year", request.getYear());
            nativeQuery.setParameter("month", request.getMonth() );
        }
        // Jpa Native Query 결과 DTO 매핑
        JpaResultMapper jpaResultMapper = new JpaResultMapper();

        // List
        List<AttendanceDto.attendanceListResponse> lResponses
            = jpaResultMapper.list(nativeQuery, AttendanceDto.attendanceListResponse.class);

        em.close();  

        return lResponses; 
    }

   

    public String findAllListNativeQuery(AttendanceDto.attendanceListRequest request) {
        StringBuilder strSQL = new StringBuilder();

        String strWhere = "";
        if (Objects.nonNull( request.getAttendanceDate() ) ) {
            strWhere += " and atd.attendance_date = :attendanceDate \n"; 
        }
        if (Objects.nonNull( request.getYear() ) && Objects.nonNull( request.getMonth() ) ) {
            strWhere += " and to_char(atd.attendance_date,'YYYY-MM-DD') like  '%' || :year || '-' || :month || '%' \n"; 
        }
    
        strSQL.append("select atd.attendance_id, atd.account_id, acct.nickname, atd.attendance_date ,Date(atd.reg_date) as regDate, Date(atd.updt_date) as updtDate, atd.use_yn, atd.meal_status, atd.location_id ,lct.location_name \n")
                .append(" from  attendance atd  \n")
                .append(" join account acct on (atd.account_id = acct.account_id)  \n")
                .append(" left join location lct on (atd.location_id = lct.location_id)  \n")
                .append(" where atd.use_yn = 'Y' and acct.use_yn='Y' ".concat(strWhere))
                .append(" order by acct.nickname  \n");
           
         
        

        return strSQL.toString();
    }
   
    @Override
    public List<AttendanceDto.attendanceStatusListResponse> findAllAttendanceStatusList(AttendanceDto.attendanceStatusListRequest request) throws Exception {
        
        EntityManager em = getEntityManager();

        // Native Query
        String strSQL = findAllAttendanceStatusListNativeQuery(request);

        // Native Query 실행
        Query nativeQuery = em.createNativeQuery(strSQL);
        if (Objects.nonNull( request.getYear() ) && Objects.nonNull( request.getMonth() )) {
            nativeQuery.setParameter("year", request.getYear());
            nativeQuery.setParameter("month", request.getMonth() );
        }
        if(Objects.nonNull( request.getAccountId())) {
            nativeQuery.setParameter("accountId", request.getAccountId());
        }
        // Jpa Native Query 결과 DTO 매핑
        JpaResultMapper jpaResultMapper = new JpaResultMapper();

        // List
        List<AttendanceDto.attendanceStatusListResponse> lResponses
            = jpaResultMapper.list(nativeQuery, AttendanceDto.attendanceStatusListResponse.class);

        em.close();

        return lResponses; 

    }

    public String findAllAttendanceStatusListNativeQuery(AttendanceDto.attendanceStatusListRequest request) {
        StringBuilder strSQL = new StringBuilder();

        String strWhere = "";
        String strWhere2 = "";
        if (Objects.nonNull( request.getYear() ) && Objects.nonNull( request.getMonth() )) {
            strWhere += " and to_char(atd.attendance_date,'YYYY-MM-DD') like  '%' || :year || '-' || :month || '%' \n"; 
        }
        
        if(Objects.nonNull( request.getAccountId())) {
            strWhere2 += " and ac.account_id = :accountId" ;
        }
        
    
        strSQL.append("select ac.account_id as accountId,ac.nickname as nickname, to_char(ac.reg_date,'YYYY-MM-DD') as reg_Date, count(attendance_id) as attendanceCount, \n")
                .append(" count(case when onoff='offline' then 1 end) as offline , count(case when onoff='online' then 1 end) as online  \n")
                .append(" from  account ac  \n")
                .append(" left join (select account_id ,attendance_id, attendance_date,")
                .append(" case when count(location_id) OVER(PARTITION BY location_id) > 1 then 'offline' ")
                .append(" else 'online' ") 
                .append(" end as onoff ") 
                .append(" from attendance atd ")
                .append(" where use_yn ='Y' ") 
                .append(" ) atd   on (ac.account_id = atd.account_id )  \n")
                .append(" where ac.use_yn='Y' ".concat(strWhere2).concat(strWhere))
                .append(" group by ac.account_id,ac.nickname  \n")
                .append(" order by offline, online ");
           
         

        return strSQL.toString();
    }
}
