package com.jxdinfo.hussar.ssdp.phy.ssdp.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.jxdinfo.hussar.ssdp.phy.ssdp.model.PersonInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 社保人员信息表 Mapper 接口
 * </p>
 *
 * @author zzj
 * @since 2019-11-15
 */
@Mapper
public interface PersonInfoMapper extends BaseMapper<PersonInfo> {
    /**
     * 插入数据
     * @param personInfo 单个人员信息
     * @return 插入结果
     */
    Integer insertAll(PersonInfo personInfo);

    /**
     * 批量插入
     * @param list 单个人员信息
     * @return 插入结果
     */
    Integer batchInsertAll(List<PersonInfo> list);


    /**
     * 获取所有的镇
     * @return 所有的镇
     */
    List<Map<String,Object>> getAllTown();

    /**
     * 根据镇获取所有的村
     * @param map 镇信息
     * @return 对应镇下面的村
     */
    List<Map<String,Object>> getAllVillage(Map<String, Object> map);

    /**
     * 以村为单位获取人员信息
     * @param map 镇信息
     * @return 对应村下面的人
     */
    List<PersonInfo> getPayableInfoByVillage(Map<String, Object> map);

    /**
     * 取出相应照片
     * @return 所有人的身份证集合
     */
    List<String> mobilePhotos();

    /**
     * 获取所有人员信息
     * @return 所有人员信息
     */
    List<PersonInfo> getAllPersonInfo();

    /**
     * 用于已导出的数据进行标记
     * @param identityId 主键
     * @return 标记结果
     */
    int updateIsUsed(@Param("identityId") String identityId);

    /**
     * 用于删除的数据进行标记
     * @param identityId 主键
     * @return 标记结果
     */
    int delPersonInfo(@Param("identityId") String identityId);
}
