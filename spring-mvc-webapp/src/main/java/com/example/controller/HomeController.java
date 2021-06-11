package com.example.controller;

import com.example.demo.dao.UserEntityDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    @Autowired
    UserEntityDAOImpl userEntityDAO;

    @RequestMapping("/")
    public String showHomePage(){
        return "index";
    }

    @RequestMapping("/list")
    public String showList(Model model){
        model.addAttribute("users",userEntityDAO.getAllUserEntities());
        return "list";
    }

    @RequestMapping("/count")
    @ResponseBody
    public String countUserEntities() {
        return userEntityDAO.getUserEntitiesCount()+"";
    }

}

