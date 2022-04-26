package com.demo.ading.demo.controller;

import com.alibaba.fastjson.JSON;
import com.demo.ading.demo.dto.*;
import com.demo.ading.demo.dto.attendance.AttendanceSingle;
import com.demo.ading.demo.service.DingAuthService;
import com.demo.ading.demo.service.DingUserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@CrossOrigin(methods = RequestMethod.POST)
@RequestMapping(value = "/ding")
public class UserController {
    static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private DingAuthService dingAuthService;

    @Autowired
    private DingUserService dingUserService;

    /**
     * 根据前台初始化后获取的免登授权码获取用户信息
     *
     * @param code   免登授权码
     * @return
     */
    @GetMapping("/login")
    public Map<String,Object> authCodeLogin(@RequestParam("code") String code) {
        DingAccessTokenDTO accessTokenDTO = dingAuthService.accessToken();
        DingUserIdDTO userIdDTO = dingUserService.getUserId(accessTokenDTO.getAccess_token(), code);
        DingUserDTO userInfo = dingUserService.getUserInfo(accessTokenDTO.getAccess_token(), userIdDTO.getUserid());


        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", code);
        result.put("token", accessTokenDTO.getAccess_token());

        result.put("user", userInfo);

        log.debug("[钉钉] 用户免登, 根据免登授权码code, corpId获取用户信息, code: {}, corpId:{}, result:{}", code, result);

        logger.info(result.toString());

        return result;
    }

    /**
     * 根据人名查询当日的考勤
     * @param personName  被查询人的系统的姓名
     * @return
     */
    @GetMapping("/getAttendanceTodaySelf")
    public Map<String, AttendanceSingle> getAttendanceDataTodaySelf(@RequestParam("personName") String personName,@RequestParam("queryDate") String queryDate){
        AttendanceSingle ycg = new AttendanceSingle(personName,
                "2020-05-11 08:14:54","2020-05-11 12:13:22");

        Map<String, AttendanceSingle> map = new HashMap<>();
        map.put("self",ycg);
        return map;
    }

    public String dateFormatChange(String datas) {
        LocalDateTime date = LocalDateTime.parse(datas, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return dateString;
    }

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String test(@RequestParam(value = "abc", required = false) String requestParam){
        return requestParam;
    }

    @RequestMapping("/test2")
    public Integer test2(@RequestBody String requestbody){
        DingAccessTokenDTO dto = (DingAccessTokenDTO) JSON.parse(requestbody);
        Integer errcode = dto.getErrcode();
        return errcode;
    }
}
