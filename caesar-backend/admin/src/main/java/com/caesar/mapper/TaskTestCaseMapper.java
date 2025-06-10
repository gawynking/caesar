package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarTaskTestCase;
import com.caesar.entity.vo.request.VerificationTestingVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface TaskTestCaseMapper extends BaseMapper<CaesarTaskTestCase> {


    @Insert("insert into caesar_task_test_case(\n" +
            "    uuid,\n" +
            "    task_id,\n" +
            "    task_name,\n" +
            "    task_version,\n" +
            "    user_id,\n" +
            "    test_code,\n" +
            "    test_result,\n" +
            "    audit_message\n" +
            ")values(\n" +
            "    #{uuid},\n" +
            "    #{taskId},\n" +
            "    #{taskName},\n" +
            "    #{taskVersion},\n" +
            "    #{userId},\n" +
            "    #{testCode},\n" +
            "    #{testResult},\n" +
            "    #{auditMessage}\n" +
            ")")
    Boolean saveTestCase(CaesarTaskTestCase taskTestCase);

    @Select("<script>" +
            "SELECT t1.* " +
            "FROM (select t1.* \n" +
            "from caesar_task_test_case t1 \n" +
            "join (\n" +
            "\tselect max(id) as id  \n" +
            "\tfrom caesar_task_test_case\n" +
            "\tgroup by task_name \n" +
            ") t2 on t1.id = t2.id) t1 " +
            "WHERE t1.user_id = #{userId} " +
            "<if test='taskName != null and taskName != \"\"'> " +
            "    AND t1.task_name LIKE CONCAT('%', #{taskName}, '%') " +
            "</if> " +
            "<if test='testResult != null'> " +
            "    AND t1.test_result = #{testResult} " +
            "</if> " +
            "ORDER BY t1.update_time DESC" +
            "</script>")
    List<CaesarTaskTestCase> getTestCases(Integer userId, String taskName, Integer testResult);

    @Update("update caesar_task_test_case set test_result = #{testResult},audit_message = #{auditMessage} where uuid = #{uuid}")
    Boolean verificationTesting(VerificationTestingVo verificationTestingVo);

}

