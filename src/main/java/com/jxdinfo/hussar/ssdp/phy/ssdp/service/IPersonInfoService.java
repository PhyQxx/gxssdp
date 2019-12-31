package com.jxdinfo.hussar.ssdp.phy.ssdp.service;

import com.baomidou.mybatisplus.service.IService;
import com.jxdinfo.hussar.ssdp.phy.ssdp.model.PersonInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 社保人员信息表 服务类
 * </p>
 *
 * @author zzj
 * @since 2019-11-15
 */
public interface IPersonInfoService extends IService<PersonInfo> {
    /**
     * 读取excel中的数据,生成list
     * @param file 导入的文档
     * @return 导入结果
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
    List<Map<String,Object>> getAllVillage(Map<String, Object> map);

    /**
     * 用于导出以村为单位的数据
     * @param response 用于输出文件
     * @param request 请求头
     * @return 每个村的文件
     */
    String villageFile(HttpServletResponse response, HttpServletRequest request);

    /**
     * 取出相应照片
     * @return 照片移动结果
     * @throws IOException 文件IO异常
     */
    Map<String,Object> mobilePhotos()throws IOException;

    /**
     * 用于导出所有有照片的数据
     * @param response 用于输出文件
     * @param request 请求头
     * @return 所有有照片的人员
     */
    String havePhoto(HttpServletResponse response, HttpServletRequest request);


    /**
     * 用于删除的数据进行标记
     * @param identityId 主键
     * @return 标记结果
     */
    int delPersonInfo(@Param("identityId") String identityId);
}
