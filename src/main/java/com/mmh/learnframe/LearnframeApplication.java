package com.mmh.learnframe;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmh.learnframe.dao.UserMapper;
import com.mmh.learnframe.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SpringBootApplication
public class LearnframeApplication {

    @Autowired
    private UserMapper userMapper;

    public static void main(String[] args) {
        SpringApplication.run(LearnframeApplication.class, args);
    }

    @GetMapping("findUserList")
    public PageInfo<User> findUserList(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.findUserList();
        PageInfo<User> pageInfo = new PageInfo<User>(userList);
        return pageInfo;
    }

}
