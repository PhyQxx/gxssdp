package com.jxdinfo.hussar.ssdp.phy.ssdp.model;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zzj
 * @since 2019-11-15
 */
@TableName("gxsb_person_info")
public class PersonInfo extends Model<PersonInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 人员识别号
     */
    @TableField("identity_number")
    private String identityNumber;
    /**
     * 身份证号
     */
    @TableId("identity_card")
    private String identityCard;
    /**
     * 名字
     */
    @TableField("name")
    private String name;
    /**
     * 性别
     */
    @TableField("sex")
    private String sex;
    /**
     * 民族
     */
    @TableField("nation")
    private String nation;
    /**
     * 出生日期
     */
    @TableField("birthday")
    private String birthday;
    /**
     * 邮编
     */
    @TableField("postcode")
    private String postcode;
    /**
     * 地址
     */
    @TableField("address")
    private String address;
    /**
     * 手机号码
     */
    @TableField("phone")
    private String phone;
    /**
     * 村镇社区/学校
     */
    @TableField("native_place")
    private String nativePlace;
    /**
     * 所属镇
     */
    @TableField("town")
    private String town;
    /**
     * 所属村
     */
    @TableField("village")
    private String village;
    /**
     * 经办机构
     */
    @TableField("agency")
    private String agency;


    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    @Override
    protected Serializable pkVal() {
        return this.identityCard;
    }

    @Override
    public String toString() {
        return "PersonInfo{" +
        "identityNumber=" + identityNumber +
        ", identityCard=" + identityCard +
        ", name=" + name +
        ", sex=" + sex +
        ", nation=" + nation +
        ", birthday=" + birthday +
        ", postcode=" + postcode +
        ", address=" + address +
        ", phone=" + phone +
        ", nativePlace=" + nativePlace +
        ", town=" + town +
        ", village=" + village +
        ", agency=" + agency +
        "}";
    }
}
