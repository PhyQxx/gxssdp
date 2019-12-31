package com.jxdinfo.hussar.ssdp.phy.ssdp.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.jxdinfo.hussar.ssdp.phy.ssdp.model.SsPersonInfo;
import com.jxdinfo.hussar.ssdp.phy.ssdp.service.ISsPersonInfoService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社保信息整理控制器
 *
 * @author 裴浩宇
 * @Date 2019-10-08 18:41:27
 */
@Controller
@RequestMapping("/phySsPersonInfo1")
public class SsPersonInfoController extends BaseController {

    private String PREFIX = "/ssdp/ssPersonInfo/";

    @Autowired
    private ISsPersonInfoService ssPersonInfoService;


    /**
     * 跳转到社保信息整理首页
     */
    @RequestMapping("/view")
    public String index() {
        return PREFIX + "ssPersonInfo.html";
    }

    /**
     * 获取社保信息整理列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition,
                       @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                       @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        Page<SsPersonInfo> page = new Page<>(pageNumber, pageSize);
        Wrapper<SsPersonInfo> ew = new EntityWrapper<>();
        Map<String, Object> result = new HashMap<>(5);
        List<SsPersonInfo> list = ssPersonInfoService.selectPage(page, ew).getRecords();
        result.put("total", page.getTotal());
        result.put("rows", list);
        return result;
    }

    /**
     * 导入excl
     */
    //导入excel
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = new HashMap<String, Object>();
        Integer[] resultMap = ssPersonInfoService.readExcelFile(file);
        map.put("count", resultMap[0]);
        map.put("time", resultMap[1]);
        return map;
    }

    /**
     * 按村为单位导出excel
     */
    @RequestMapping("/exportVillageFile")
    @ResponseBody
    public Map<String, Object> exportVillageFile(HttpServletResponse response, HttpServletRequest request) {
        String result = "";
        try {
            result = ssPersonInfoService.VillageFile(response, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", result);
        return map;
    }

    /**
     * 取出相应照片
     */
    @RequestMapping("/mobilephotos")
    @ResponseBody
    public Map<String, Object> mobilePhotos(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map = ssPersonInfoService.mobilePhotos();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }








    /**
     * 跳转到添加社保信息整理
     *//*
    @RequestMapping("/ssPersonInfo_add")
    public String ssPersonInfoAdd() {
        return PREFIX + "ssPersonInfo_add.html";
    }

    *//**
     * 跳转到修改社保信息整理
     *//*
    @RequestMapping("/ssPersonInfo_update/{ssPersonInfoId}")
    public String ssPersonInfoUpdate(@PathVariable String ssPersonInfoId, Model model) {
        SsPersonInfo ssPersonInfo = ssPersonInfoService.selectById(ssPersonInfoId);
        model.addAttribute("item",ssPersonInfo);
        LogObjectHolder.me().set(ssPersonInfo);
        return PREFIX + "ssPersonInfo_edit.html";
    }*/

    /**
     * 新增社保信息整理
     *//*
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(SsPersonInfo ssPersonInfo) {
        ssPersonInfoService.insert(ssPersonInfo);
        return SUCCESS_TIP;
    }

    *//**
     * 删除社保信息整理
     *//*
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String ssPersonInfoId) {
        ssPersonInfoService.deleteById(ssPersonInfoId);
        return SUCCESS_TIP;
    }

    *//**
     * 修改社保信息整理
     *//*
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(SsPersonInfo ssPersonInfo) {
        ssPersonInfoService.updateById(ssPersonInfo);
        return SUCCESS_TIP;
    }

    *//**
     * 社保信息整理详情
     *//*
    @RequestMapping(value = "/detail/{ssPersonInfoId}")
    @ResponseBody
    public Object detail(@PathVariable("ssPersonInfoId") String ssPersonInfoId) {
        return ssPersonInfoService.selectById(ssPersonInfoId);
    }*/
}
