/**
 * 管理初始化
 */
var SsPersonInfo = {
    id: "SsPersonInfoTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    pageSize:20,
    pageNumber:1
};
layui.use(['layer','bootstrap_table_edit','Hussar', 'HussarAjax','upload'], function(){
	var layer = layui.layer
	    ,table = layui.table
        ,Hussar = layui.Hussar
	    ,$ = layui.jquery
        ,upload = layui.upload
	    ,$ax = layui.HussarAjax;


/**
 * 初始化表格的列
 */
SsPersonInfo.initColumn = function () {
    return [
        {checkbox:true, halign:'center',align:"center",width: 50},
        {title: '序号',align:"center" ,halign:'center',width:50 ,formatter: function (value, row, index) {return (SsPersonInfo.pageNumber-1)*SsPersonInfo.pageSize +1 +index ;}},
            {title: '身份证', field: 'identityCard', align: 'center',halign:'center'},
            {title: '姓名', field: 'name', align: 'center',halign:'center'},
            {title: '籍贯', field: 'nativePlace', align: 'center',halign:'center'},
            {title: '镇', field: 'town', align: 'center',halign:'center'},
            {title: '村', field: 'village', align: 'center',halign:'center'}
    ];
};

/**
 * 检查是否选中
 */
SsPersonInfo.check = function () {
    var selected = $('#SsPersonInfoTable').bootstrapTable('getSelections');
    if(selected.length == 0){
        Hussar.info("请先选中表格中的某一记录！");
        return false;
    }else{
        SsPersonInfo.seItem = selected[0];
        return true;
    }
};


//选完文件后不自动上传
    upload.render({
        elem: '#optionFile',
        url: '/ssPersonInfo/uploadExcel',
        accept: 'file', //普通文件
        auto: false,
        //,multiple: true
        bindAction: '#uploadFile',
        done: function(res){
            console.log(res);
        }
    });

    /**
     * 根据乡村导出
     */
    $('#villageBtn').on('click',function () {
       $.ajax({
           async:false,
           type:"post",
           traditional:true,
           contentType:'application/x-www-form-urlencoded',
           url:Hussar.ctxPath+'/ssPersonInfo/exportVillageFile',
           data:{ },
           dataType:"json",
           success:function (data) {
           },
           error:function (data) {

           }
       })
    });


    /**
 * 查询列表
 */
SsPersonInfo.search = function () {
    $('#SsPersonInfoTable').bootstrapTable('refresh');
};

$(function () {
    var defaultColunms = SsPersonInfo.initColumn();

    $('#SsPersonInfoTable').bootstrapTable({
            dataType:"json",
            url:'/ssPersonInfo/list',
            pagination:true,
            pageList:[10,15,20,50,100],
            striped:true,
            pageSize:20,
            queryParamsType:'',
            columns: defaultColunms,
            height:$("body").height() - $(".layui-form").outerHeight(true) - 26,
            sidePagination:"server",
            onPageChange:function(number, size){SsPersonInfo.pageNumber = number ; SsPersonInfo.pageSize = size}
        });
})

});