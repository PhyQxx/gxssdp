/**
 * 初始化社保信息整理详情对话框
 */
var SsPersonInfoInfoDlg = {
    ssPersonInfoInfoData : {}
};

layui.use(['layer', 'Hussar', 'HussarAjax', 'laydate'], function(){
	var layer = layui.layer
        ,Hussar = layui.Hussar
	    ,$ = layui.jquery
	    ,laydate = layui.laydate
	    ,$ax = layui.HussarAjax;

/**
 * 清除数据
 */
SsPersonInfoInfoDlg.clearData = function() {
    this.ssPersonInfoInfoData = {};
};

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
SsPersonInfoInfoDlg.set = function(key, val) {
    this.ssPersonInfoInfoData[key] = (typeof val == "undefined") ? $("#" + key).val() : val;
    return this;
};

/**
 * 设置对话框中的数据
 *
 * @param key 数据的名称
 * @param val 数据的具体值
 */
SsPersonInfoInfoDlg.get = function(key) {
    return $("#" + key).val();
};

/**
 * 关闭此对话框
 */
SsPersonInfoInfoDlg.close = function() {
    parent.layui.layer.close(window.parent.SsPersonInfo.layerIndex);
};

/**
 * 收集数据
 */
SsPersonInfoInfoDlg.collectData = function() {
    this
    .set('identityCard')
    .set('name')
    .set('nativePlace')
    .set('town')
    .set('village');
};

/**
 * 提交添加
 */
SsPersonInfoInfoDlg.addSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Hussar.ctxPath + "/ssPersonInfo/add", function(data){
        window.parent.layui.Hussar.success("添加成功!");
        window.parent.$('#SsPersonInfoTable').bootstrapTable('refresh');
        SsPersonInfoInfoDlg.close();
    },function(data){
        Hussar.error("添加失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.ssPersonInfoInfoData);
    ajax.start();
};

/**
 * 提交修改
 */
SsPersonInfoInfoDlg.editSubmit = function() {

    this.clearData();
    this.collectData();

    //提交信息
    var ajax = new $ax(Hussar.ctxPath + "/ssPersonInfo/update", function(data){
        window.parent.layui.Hussar.success("修改成功!");
        window.parent.$('#SsPersonInfoTable').bootstrapTable('refresh');
        SsPersonInfoInfoDlg.close();
    },function(data){
        Hussar.error("修改失败!" + data.responseJSON.message + "!");
    });
    ajax.set(this.ssPersonInfoInfoData);
    ajax.start();
};

/**
 * 初始化时间控件
 */
SsPersonInfoInfoDlg.initLaydate = function() {
    var dateDom = $(".dateType");
    $.each($(".dateType"), function (i,dom) {
        laydate.render({
            elem: dom,
            type : 'datetime'
        });
    });
}

$(function() {
    SsPersonInfoInfoDlg.initLaydate();   //初始化时间控件
});

});