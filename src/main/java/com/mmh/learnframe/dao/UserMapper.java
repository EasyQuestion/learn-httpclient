package com.mmh.learnframe.dao;

import com.mmh.learnframe.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author muminghui
 * @date 2019/7/11 17:06
 */
@Mapper
public interface UserMapper {

    @Select("select USER_ID_NO userNo,USER_NAME username,USER_SEX sex,USER_AGE age,USER_STATE state from user")
    List<User> findUserList();
}
