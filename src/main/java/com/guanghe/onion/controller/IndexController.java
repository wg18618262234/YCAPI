package com.guanghe.onion.controller;

/**
 * Created by renjie on 2018/12/5.
 */

import com.guanghe.onion.dao.ApiJPA;
import com.guanghe.onion.dao.ErrorLogJPA;
import com.guanghe.onion.dao.MonitorLogJPA;
import com.guanghe.onion.dao.StaticsJPA;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.exceptions.TemplateInputException;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static io.restassured.RestAssured.given;

@Controller
public class IndexController {

    @Autowired
    private ApiJPA apiJPA;
    @Autowired
    private ErrorLogJPA errorlogjpa;
    @Autowired
    private MonitorLogJPA monitorlogjpa;
    @Autowired
    private StaticsJPA staticsjpa;

    @RequestMapping("index")
    public String index(Model model) throws TemplateInputException {
        return "index";
    }

    @RequestMapping("statics")
    public String statics(Model model, @RequestParam(value = "code", required = false) String code,
                          @RequestParam(value = "login", required = false) boolean login, HttpSession session
    ) throws TemplateInputException {

        if (!login) {
            String gettokenUrl = "http://backstage-test.yc345.tv:3003/api/auth/code/" + code;
            String meapi_url = "http://backstage-test.yc345.tv:3003/api/auth/me";

            Response result = given().get(gettokenUrl);
            String response_body = result.getBody().asString();
            System.out.println("response_body:" + response_body);

            String onionsToken = result.jsonPath().get("onionsToken");
            String shadowToken = result.jsonPath().get("shadowToken");

            HashMap header = new HashMap<>();
            header.put("Authorization", onionsToken);
            header.put("ShadowAuthorization", shadowToken);

            Response result2 = given().headers(header).get(meapi_url);
            String name = result2.jsonPath().getString("name");
            String mail = result2.jsonPath().getString("mail");
            int status = result2.jsonPath().getInt("status");

            model.addAttribute("Authorization", onionsToken);
            model.addAttribute("ShadowAuthorization", shadowToken);
            model.addAttribute("name", name);
            model.addAttribute("mail", mail);
            model.addAttribute("status", status);
            session.setAttribute("name", name);
//            session.setAttribute("mail", mail);
            session.setAttribute("user", mail.substring(0, mail.indexOf("@")));
        }


        //以下是统计数据和登录无关
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
//        String endDate = sdf.format(today);//当前日期
        //获取三十天前日期
        Calendar theCa = Calendar.getInstance();
        theCa.setTime(today);
        theCa.add(Calendar.DATE, -30);//最后一个数字30可改，30天的意思
        Date start = theCa.getTime();
        String startDate = sdf.format(start);//三十天之前日期
        long apisum = apiJPA.count();
        model.addAttribute("apisum", apisum);

        int errorsOf30days=staticsjpa.errorsOf30days(startDate);
        int longExecTimeOf30days=staticsjpa.longExecTimeOf30days(startDate, Long.valueOf(5000));
        int sum_500Of30days=staticsjpa.sum_500Of30days(startDate);
        int discardApiNums=staticsjpa.discardApiNums();
        model.addAttribute("errorsOf30days", errorsOf30days);
        model.addAttribute("longExecTimeOf30days", longExecTimeOf30days);
        model.addAttribute("sum_500Of30days", sum_500Of30days);
        model.addAttribute("discardApiNums", discardApiNums);
        // 统计数据和登录无关

        return "statics";
    }


    @RequestMapping("/")
    public String index2() {
        return "redirect:/list";
    }


    @RequestMapping("/hello")
    public String hello(Model model, @RequestParam(value="name", required=false, defaultValue="World") String name) {
        model.addAttribute("name", name);
        return "hello";
    }

    @RequestMapping("/myerror")
    public String error(Model model, String msg) {
        model.addAttribute("msg", msg);
        return "error";
    }


//    @RequestMapping(value = "user/login_view",method = RequestMethod.GET)
//    public String login_view(){
//        return "login";
//    }
//
//



}