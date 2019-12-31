package com.jxdinfo.hussar.ssdp.phy.ssdp.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.ssdp.phy.ssdp.model.PersonInfo;
import com.jxdinfo.hussar.ssdp.phy.ssdp.model.SsPersonInfo;
import com.jxdinfo.hussar.ssdp.phy.ssdp.service.IPersonInfoService;
import com.jxdinfo.hussar.ssdp.phy.ssdp.service.ISsPersonInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
 * @author zzj
 * @Date 2019-10-08 18:41:27
 */
@Controller
@RequestMapping("/phySsPersonInfo")
public class PersonInfoController extends BaseController {

    private String PREFIX = "/ssdp/ssPersonInfo/";

    @Autowired
    private IPersonInfoService personInfoService;


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
        Page<PersonInfo> page = new Page<>(pageNumber, pageSize);
        Wrapper<PersonInfo> ew = new EntityWrapper<>();
        ew.like("identity_card",condition).eq("is_used","2");
        Map<String, Object> result = new HashMap<>(5);
        List<PersonInfo> list = personInfoService.selectPage(page, ew).getRecords();
        result.put("total", page.getTotal());
        result.put("rows", list);
        return result;
    }

    /**
     * 导入excl
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = new HashMap<String, Object>();
        Integer[] resultMap = personInfoService.readExcelFile(file);
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
            result = personInfoService.villageFile(response, request);
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
            map = personInfoService.mobilePhotos();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    @RequestMapping("/havePhoto")
    @ResponseBody
    public Map<String, Object> havePhoto(HttpServletResponse response, HttpServletRequest request) {
        String result = "";
        try {
            result = personInfoService.havePhoto(response, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", result);
        return map;
    }


    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String personInfoId) {
       personInfoService.delPersonInfo(personInfoId);
        return SUCCESS_TIP;
    }

}
