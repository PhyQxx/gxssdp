/**
 * 社保信息整理管理初始化
 */
var SsPersonInfo = {
    id: "SsPersonInfoTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1,
    pageSize:20,
    pageNumber:1
};
layui.use(['layer','bootstrap_table_edit','Hussar', 'HussarAjax'], function(){
	var layer = layui.layer
	    ,table = layui.table
        ,Hussar = layui.Hussar
	    ,$ = layui.jquery
	    ,$ax = layui.HussarAjax;
    function getInfo () {
        var selected = $('#SsPersonInfoTable').bootstrapTable('getSelections');
        if(selected.length == 0){
            Hussar.info("请先选中表格中的某一记录！");
            return false;
        }else{
            SsPersonInfo.seItem = selected[0];
            return true;
        }
    }
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
SsPersonInfo.check = getInfo ();

    /**
     * 导入
     */
    var excelImport = document.getElementById("excelImport");
    excelImport.onclick = function() {
        layer.open({
            type: 1,
            content: $('#choose'), //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响

        });
    }
    var User = function() {
        this.init = function() {
            //模拟上传excel
            $("#uploadEventBtn").unbind("click").bind("click", function() {
                $("#uploadEventFile").click();
            });
            $("#uploadEventFile").bind("change", function() {
                $("#uploadEventPath").attr("value",    $("#uploadEventFile").val());
            });
        };
        //点击上传钮
        var uploadBtn = document.getElementById("uploadBtn");
        uploadBtn.onclick = function() {
            var uploadEventFile = $("#uploadEventFile").val();
            if (uploadEventFile == '') {
                alert("请择excel,再上传");
            } else if (uploadEventFile.lastIndexOf(".xls") < 0) {//可判断以.xls和.xlsx结尾的excel
                alert("只能上传Excel文件");
            } else {
                var url = Hussar.ctxPath+"/phySsPersonInfo/import";

                var formData = new FormData($('form')[0]);
                user.sendAjaxRequest(url, "POST", formData);
            }

        };
        this.sendAjaxRequest = function(url, type, data) {
            $.ajax({
                url : url,
                type : type,
                data : data,
                dataType : "json",
                success : function(result) {
                    if (result.count != null ) {
                        alert("成功导入"+result.count+"条数据! \n共耗时"+
                            result.time+"毫秒!");
                        layer.closeAll();
                        $('#SsPersonInfoTable').bootstrapTable('refresh');
                    }else {
                        alert("导入失败!");
                        layer.closeAll();
                        $('#SsPersonInfoTable').bootstrapTable('refresh');
                    }


                },
                error : function() {
                    alert("导入出错!!!");
                    layer.closeAll();
                    $('#SsPersonInfoTable').bootstrapTable('refresh');

                },
                cache : false,
                contentType : false,
                processData : false
            });
        };
    };
    var user;
    $(function() {
        user = new User();
        user.init();
    });



    var exportBtn = document.getElementById("excelExport");
    exportBtn.onclick = function() {
        if (confirm("确定导出?")) {
            $.ajax({
                url : Hussar.ctxPath+"/phySsPersonInfo/exportVillageFile",
                type : "post",
                data:{},
                dataType :"json",
                success : function(result) {
                    alert(result.message);
                },
                error : function(result) {
                    // alert(result.message);
                    alert("导出失败---");
                },
            });
        }

    }

    $('#mobilephotos').bind('click',function () {
        if (confirm("确定导出?")) {
            $.ajax({
                url : Hussar.ctxPath+"/phySsPersonInfo/mobilephotos",
                type : "post",
                data:{},
                dataType :"json",
                success : function(result) {
                    alert(result.message+" \n耗时:"+result.time);
                },
                error : function(result) {
                    // alert(result.message);
                    alert("导出失败---");
                },
            });
        }
    });

    $('#havePhoto').bind('click',function () {
        if (confirm("确定导出?")) {
            $.ajax({
                url : Hussar.ctxPath+"/phySsPersonInfo/havePhoto",
                type : "post",
                data:{},
                dataType :"json",
                success : function(result) {
                    alert(result.message);
                },
                error : function(result) {
                    // alert(result.message);
                    alert("导出失败---");
                }
            });
        }
    });


























// /**
//  * 点击添加社保信息整理
//  */
// SsPersonInfo.openAddSsPersonInfo = function () {
//     var index = layer.open({
//         type: 2,
//         title: '添加社保信息整理',
//         area: ['400px', '420px'], //宽高
//         fix: false, //不固定
//         maxmin: false,
//         content: Hussar.ctxPath + '/ssPersonInfo/ssPersonInfo_add'
//     });
//     this.layerIndex = index;
// };
//
// /**
//  * 点击修改社保信息整理
//  */
// SsPersonInfo.openSsPersonInfoDetail = function () {
//     if (this.check()) {
//         var index = layer.open({
//             type: 2,
//             title: '社保信息整理详情',
//             area: ['400px', '420px'], //宽高
//             fix: false, //不固定
//             maxmin: false,
//             content: Hussar.ctxPath + '/ssPersonInfo/ssPersonInfo_update/' +              });
//         this.layerIndex = index;
//     }
// };
//
/**
 * 删除社保信息整理
 */
$('#delInfo').on('click',function () {
    if (getInfo()) {
        var ajax = new $ax(Hussar.ctxPath + "/phySsPersonInfo/delete", function (data) {
            Hussar.success("删除成功!");
            $('#SsPersonInfoTable').bootstrapTable('refresh');
        }, function (data) {
            Hussar.error("删除失败!" + data.responseJSON.message + "!");
        });
        ajax.set("personInfoId",SsPersonInfo.seItem.identityCard);
        ajax.start();
    }
});


/**
 * 查询社保信息整理列表
 */
SsPersonInfo.search = function () {
    $('#SsPersonInfoTable').bootstrapTable('refresh');
};

$(function () {
    var defaultColunms = SsPersonInfo.initColumn();

    $('#SsPersonInfoTable').bootstrapTable({
            dataType:"json",
            url:Hussar.ctxPath+'/phySsPersonInfo/list',
            pagination:true,
            pageList:[10,15,20,50,100],
            striped:true,
            pageSize:20,
            queryParams: queryPrams,
            queryParamsType:'',
            columns: defaultColunms,
            height:$("body").height() - $(".layui-form").outerHeight(true) - 26,
            sidePagination:"server",
            onPageChange:function(number, size){SsPersonInfo.pageNumber = number ; SsPersonInfo.pageSize = size}
    });
    function queryPrams(){
        var name = $("#condition").val();
        var temp = {
            condition: name,
            pageSize: this.pageSize,
            pageNumber: this.pageNumber
        };
        return temp;
    }

})

});