package com.dfc.ind.controller.station.sys;

import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.entity.vo.SysUserActiveVo;
import com.dfc.ind.mapper.sys.SysUserActiveMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author
 * @program: shfactory
 * @description: 用户活跃度
 * @create: 2022-12-17 18-23
 **/
@Slf4j
@RestController
@RequestMapping("/statisticsUserActive")
public class SysUserActiveController {

    @Autowired
    SysUserActiveMapper sysUserActiveMapper;
    /**
     * @Author:
     * @Description: 用户活跃度查询
     */
    @PostMapping
    public List statisticsUserActive(@RequestBody SysUserActiveVo entity){
        log.info("input："+entity);
        Integer day = getDay(entity);
        //查询所有操作商户信息
        List<SysUserActiveVo> resultList = sysUserActiveMapper.selectStatisticsUserActive(entity);
        List<SysUserActiveVo> userResultList=new ArrayList();
        for (SysUserActiveVo sysUserActiveVo : resultList) {
            //计算用户活跃量
            double operNum =(double)sysUserActiveVo.getOperNum()/day;
            double userActiveAmount = Double.parseDouble(String.format("%.3f", operNum));
            sysUserActiveVo.setUserActiveAmount(userActiveAmount);
            //计算用户活跃数.
            Integer operUserNum = sysUserActiveVo.getOperUserNum();
            double userNumberActive = (double) operUserNum / day;
            double userNumberActive1 = Double.parseDouble(String.format("%.3f", userNumberActive));
            sysUserActiveVo.setUserNumberActive(userNumberActive1);
            //计算用户活跃度
            //总人数
            Integer userNum = sysUserActiveVo.getUserNum();
            double userActive = (double) operUserNum / userNum / day;
            double userActive1 = Double.parseDouble(String.format("%.3f", userActive));
            sysUserActiveVo.setUserActive(userActive1);
            //设置操作时间
            sysUserActiveVo.setOperTime(entity.getStartTime());
            userResultList.add(sysUserActiveVo);
        }
        log.info("output："+userResultList);
        return resultList;
    }

    /**
     * @Author:
     * @Description: 计算时间差值
     */
    public Integer getDay(SysUserActiveVo entity){
        Integer day=1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(entity.getStartTime())&& StringUtils.isEmpty(entity.getEndTime())){
            Date date = new Date();
            Date date1 = DateUtils.addDays(date, 0);
            Date date2 = DateUtils.addDays(date, 1);
            entity.setStartTime(dateFormat.format(date1));
            entity.setEndTime(dateFormat.format(date2));
        }
        try {
            String endTime = entity.getEndTime();
            String startTime = entity.getStartTime();
            Date startDateTime = dateFormat.parse(startTime);
            Date endDateTime = dateFormat.parse(endTime);
            Long time = endDateTime.getTime() - startDateTime.getTime();
            Long date=time/(1000*60*60*24);
            day = date.intValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

}
