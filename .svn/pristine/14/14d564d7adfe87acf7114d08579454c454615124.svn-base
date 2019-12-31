package com.jxdinfo.hussar.ssdp.phy.ssdp.service;

import com.jxdinfo.hussar.ssdp.phy.ssdp.model.SsPersonInfo;
import com.baomidou.mybatisplus.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 社保人员信息表 服务类
 * </p>
 *
 * @author 裴浩宇
 * @since 2019-10-08
 */
public interface ISsPersonInfoService extends IService<SsPersonInfo> {
    /**
     * 读取excel中的数据,生成list
     */
    Integer[] readExcelFile(MultipartFile file);


    /**
     * 获取所有的乡镇/街道
     * @return 所有的乡镇/街道
     */
    List<Map<String,Object>> getAllTown();

    /**
     * 根据乡镇获取所辖的村/社区
     * @param map 乡镇
     * @return 本镇所有的村
     */
    List<Map<String,Object>> getAllVillage(Map<String,Object>map);

    /**
     * 用于导出以村为单位的数据
     * @param response 用于输出文件
     */
    String VillageFile(HttpServletResponse response, HttpServletRequest request);

    /**
     * 取出相应照片
     */
    Map<String,Object> mobilePhotos()throws IOException;
}
