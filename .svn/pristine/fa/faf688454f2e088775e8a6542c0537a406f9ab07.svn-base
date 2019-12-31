package com.jxdinfo.hussar.ssdp.phy.ssdp.dao;

import com.jxdinfo.hussar.ssdp.phy.ssdp.model.SsPersonInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 社保人员信息表 Mapper 接口
 * </p>
 *
 * @author 裴浩宇
 * @since 2019-10-08
 */
@Mapper
public interface SsPersonInfoMapper extends BaseMapper<SsPersonInfo> {
    /**
     * 插入数据
     * */
    Integer insertAll(SsPersonInfo ssPersonInfo);

    /**
     * 批量插入
     */
    Integer batchInsertAll(List<SsPersonInfo> list);


    /**
     * 获取所有的镇
     */
    List<Map<String,Object>> getAllTown();

    /**
     * 根据镇获取所有的村
     */
    List<Map<String,Object>> getAllVillage(Map<String,Object> map);

    /**
     * 以村为单位获取人员信息
     */
    List<SsPersonInfo> getPayableInfoByVillage(Map<String,Object> map);

    /**
     * 取出相应照片
     */
    List<String> mobilePhotos();
}
