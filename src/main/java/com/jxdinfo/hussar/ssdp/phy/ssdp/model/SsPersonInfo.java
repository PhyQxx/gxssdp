package com.jxdinfo.hussar.ssdp.phy.ssdp.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 社保人员信息表
 * </p>
 *
 * @author 裴浩宇
 * @since 2019-10-08
 */
@TableName("ss_person_info")
public class SsPersonInfo extends Model<SsPersonInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 身份证
     */
    @TableField("identity_card")
    private String identityCard;
    /**
     * 姓名
     */
    @TableField("name")
    private String name;
    /**
     * 籍贯
     */
    @TableField("native_place")
    private String nativePlace;
    /**
     * 镇
     */
    @TableField("town")
    private String town;
    /**
     * 村
     */
    @TableField("village")
    private String village;


    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    @Override
    protected Serializable pkVal() {
        return this.serialVersionUID;
    }

    @Override
    public String toString() {
        return "SsPersonInfo{" +
        "identityCard=" + identityCard +
        ", name=" + name +
        ", nativePlace=" + nativePlace +
        ", town=" + town +
        ", village=" + village +
        "}";
    }
}
