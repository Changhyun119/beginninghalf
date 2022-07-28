package com.toy.attendance.dev.model.attendance.repository;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

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

        // Native Query 실행
        Query nativeQuery = em.createNativeQuery(strSQL);

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

        String strWhere = "\n";
        if (Objects.nonNull( request.getAttendanceDate() ) ) {
            strWhere = "and atd.attendance_date ='" + request.getAttendanceDate() +"' \n";
        }
    
        strSQL.append("select atd.attendance_id, atd.account_id, acct.nickname, atd.attendance_date ,Date(atd.reg_date) as regDate, Date(atd.updt_date) as updtDate, atd.use_yn, atd.meal_status, atd.location_id ,lct.location_name \n")
                .append(" from  attendance atd  \n")
                .append(" join account acct on (atd.account_id = acct.account_id)  \n")
                .append(" left join location lct on (atd.location_id = lct.location_id)  \n")
                .append(" where atd.use_yn = 'Y' and acct.use_yn='Y' ".concat(strWhere))
                .append(" order by acct.nickname  \n");
           
         
        

        return strSQL.toString();
    }
}
