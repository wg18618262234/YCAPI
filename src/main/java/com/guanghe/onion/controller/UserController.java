package com.guanghe.onion.controller;

/**
 * Created by renjie on 2018/12/5.
 */
import com.guanghe.onion.dao.UserJPA;

import com.guanghe.onion.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.exceptions.TemplateInputException;

import java.util.List;

@RestController
public class UserController {


    @Autowired
    private UserJPA userJPA;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public List<UserEntity> list(){
        return userJPA.findAll();
    }


    @RequestMapping(value = "/save",method = RequestMethod.GET)
    public UserEntity save(UserEntity user){
        return userJPA.save(user);
    }

    @RequestMapping(value = "/add")
    public String add()
    {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("测试");
        userEntity.setAddress("测试地址");
        userEntity.setAge(21);
        userJPA.save(userEntity);
        return "用户信息添加成功";
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public List<UserEntity> delete(Long id)
    {
        userJPA.deleteById(id);
        return userJPA.findAll();
    }
}