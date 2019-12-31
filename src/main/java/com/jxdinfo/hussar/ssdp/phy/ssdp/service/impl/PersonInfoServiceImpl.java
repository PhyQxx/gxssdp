package com.jxdinfo.hussar.ssdp.phy.ssdp.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.ssdp.phy.ssdp.dao.PersonInfoMapper;
import com.jxdinfo.hussar.ssdp.phy.ssdp.model.PersonInfo;
import com.jxdinfo.hussar.ssdp.phy.ssdp.service.IPersonInfoService;

import com.jxdinfo.hussar.util.ExcelSaxReader;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 社保人员信息表 服务实现类
 * </p>
 *
 * @author 裴浩宇
 * @since 2019-10-08
 */
@Service
public class PersonInfoServiceImpl extends ServiceImpl<PersonInfoMapper, PersonInfo> implements IPersonInfoService {

    @Autowired
    PersonInfoMapper personInfoMapper;
    @Autowired
    ExcelSaxReader excelSaxReader;

    @Override
    public Integer[] readExcelFile(@RequestParam("file") MultipartFile file){

            //用于存放导入的结果信息
            Integer[] resultMap = {0,0};
            InputStream inputStream=null;
            try{
                //新增的数据条数
                int count = 0;
                //新增的多个map
                List<PersonInfo> personInfos = new ArrayList<>();
                long startTime = System.currentTimeMillis();
                //转换为输入流
                inputStream = file.getInputStream();
                ExcelSaxReader reader = excelSaxReader.parse(inputStream);
                List<String[]> datas = reader.getDatas();
                for(String[] str : datas){
                    PersonInfo personInfo = new PersonInfo();
                    personInfo.setIdentityNumber(str[0]);
                    personInfo.setIdentityCard(str[1]);
                    personInfo.setName(str[2]);
                    personInfo.setSex(str[3]);
                    personInfo.setNation(str[4]);
                    personInfo.setBirthday(str[5]);
                    personInfo.setPostcode(str[6]);
                    personInfo.setAddress(str[7]);
                    personInfo.setPhone(str[8]);
                    personInfo.setNativePlace(str[17]);
                    personInfo.setAgency(str[18]);
                    // 根据籍贯截取出镇
                    int index = str[17].indexOf(" ");
                    int lastIndexOf = str[17].lastIndexOf(" ");
                    String town = str[17].substring(index, lastIndexOf);
                    personInfo.setTown(town);
                     //根据籍贯截取出村
                    String village = str[17].substring(lastIndexOf);
                    personInfo.setVillage(village);
                    personInfos.add(personInfo);
                }
                long endTime = System.currentTimeMillis();
                //读取Excel耗时            =============45s左右
                long time = endTime-startTime;
                long startTime2 = System.currentTimeMillis();
                resultMap[0] = personInfoMapper.batchInsertAll(personInfos);
                long endTime2 = System.currentTimeMillis();
                //执行插入耗时             =============57s左右
                long time2 = endTime2-startTime2;
                long allTime = time+time2;
                resultMap[1] = (int)allTime;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (inputStream!=null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return resultMap;
        }

    /**
     * 获取所有的乡镇/街道
     * @return 所有的乡镇/街道
     */
    @Override
    public List<Map<String, Object>> getAllTown() {
        return personInfoMapper.getAllTown();
    }

    /**
     * 根据乡镇获取所辖的村/社区
     * @param map 乡镇
     * @return 本镇所有的村
     */
    @Override
    public List<Map<String, Object>> getAllVillage(Map<String, Object> map) {
        return personInfoMapper.getAllVillage(map);
    }

    /**
     * 用于导出以村为单位的数据
     * @param response 用于输出文件
     */
    @Value("${excelPath}")
    private String excelPath;
    @Value("${photoPath}")
    private String photoPath;
    @Override
    public String villageFile(HttpServletResponse response, HttpServletRequest request) {
        String result;
        List<Map<String, Object>> allTowns = personInfoMapper.getAllTown();
        String dirPath = excelPath;
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = new ThreadPoolExecutor(5,10,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(10),
                new ThreadPoolExecutor.AbortPolicy());
        for (Map<String, Object> town : allTowns){
            executorService.execute(()->{
                System.out.println("正在导出---");
                //获取乡镇名称
                String Town = (String) town.get("Town");
                //获取乡镇下辖的村
                List<Map<String, Object>> allVillages = personInfoMapper.getAllVillage(town);
                for (Map<String, Object> village : allVillages){
                    Map<String,Object> param = new HashMap<>(5);
                    String Village = (String) village.get("Village");

                    param.put("Town",Town);
                    param.put("Village",Village);
                    //填入Excel表格中的数据
                    List<PersonInfo> personInfos = personInfoMapper.getPayableInfoByVillage(param);
                    String fileName = Village+"社保人员清单.xls";
                    String path = dirPath+"\\"+Town+"\\";
                    File targetFile = new File(path);
                    if(!targetFile.exists()){//如果文件夹不存在
                        targetFile.mkdirs();
                    }
                    //response.setContentType("octets/stream");
                    try {
                        FileOutputStream fos = new FileOutputStream(new File(path+fileName));
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        HSSFWorkbook wb = new HSSFWorkbook();
                        String sheetName=Village+"社保人员清单";
                        HSSFSheet sheet = wb.createSheet(sheetName);
                        HSSFCellStyle cs = wb.createCellStyle();
                        cs.setAlignment(HorizontalAlignment.CENTER);
                        cs.setVerticalAlignment(VerticalAlignment.CENTER);
                        // 画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
                        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

                        /* 设置打印格式 */
                        HSSFPrintSetup hps = sheet.getPrintSetup();
                        hps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
                        hps.setLandscape(true);
                        hps.setFitHeight((short) 1);
                        hps.setFitWidth((short) 1);
                        hps.setScale((short) 65);
                        hps.setFooterMargin(0);
                        hps.setHeaderMargin(0);
                        sheet.setMargin(HSSFSheet.TopMargin, 0.3);
                        sheet.setMargin(HSSFSheet.BottomMargin, 0);
                        sheet.setMargin(HSSFSheet.LeftMargin, 0.3);
                        sheet.setMargin(HSSFSheet.RightMargin, 0);


                        //创建第一行
                        HSSFRow row = sheet.createRow((short) 0);
                        HSSFCell cell ;
                        row.setHeightInPoints(20);

                        HSSFFont font = wb.createFont();
                        font.setFontName("宋体");
                        //粗体显示
//                        font.setBold(true);
                        font.setFontHeightInPoints((short) 12);
                        cs.setFont(font);
                        cell = row.createCell(0);
                        cell.setCellStyle(cs);
                        cell.setCellValue("人员标识（不可修改）");
                        cell = row.createCell(1);
                        cell.setCellStyle(cs);
                        cell.setCellValue("身份证号码（不可修改）");
                        cell = row.createCell(2);
                        cell.setCellStyle(cs);
                        cell.setCellValue("姓名（不可修改）");
                        cell = row.createCell(3);
                        cell.setCellStyle(cs);
                        cell.setCellValue("性别（必填）");
                        cell = row.createCell(4);
                        cell.setCellStyle(cs);
                        cell.setCellValue("民族（必填）");
                        cell = row.createCell(5);
                        cell.setCellStyle(cs);
                        cell.setCellValue("出生日期（必填）");
                        cell = row.createCell(6);
                        cell.setCellStyle(cs);
                        cell.setCellValue("邮政编码（必填）");
                        cell = row.createCell(7);
                        cell.setCellStyle(cs);
                        cell.setCellValue("通讯地址（必填）");
                        cell = row.createCell(8);
                        cell.setCellStyle(cs);
                        cell.setCellValue("手机号码（必填）");
                        cell = row.createCell(9);
                        cell.setCellStyle(cs);
                        cell.setCellValue("村镇社区/学校");
                        cell = row.createCell(10);
                        cell.setCellStyle(cs);
                        cell.setCellValue("所属镇");
                        cell = row.createCell(11);
                        cell.setCellStyle(cs);
                        cell.setCellValue("所属村");
                        cell = row.createCell(12);
                        cell.setCellStyle(cs);
                        cell.setCellValue("经办机构名称");
                        cell = row.createCell(13);
                        cell.setCellStyle(cs);
                        cell.setCellValue("照片");
                        cell.setCellStyle(cs);
                        sheet.setColumnWidth(0, 7168);
                        sheet.setColumnWidth(1, 7168);
                        sheet.setColumnWidth(2, 5120);
                        sheet.setColumnWidth(3, 5120);
                        sheet.setColumnWidth(4, 5120);
                        sheet.setColumnWidth(5, 5120);
                        sheet.setColumnWidth(6, 5120);
                        sheet.setColumnWidth(7, 5120);
                        sheet.setColumnWidth(8, 5120);
                        sheet.setColumnWidth(9, 6124);
                        sheet.setColumnWidth(10, 4096);
                        sheet.setColumnWidth(11, 4096);
                        sheet.setColumnWidth(12, 5120);
                        //设置列值-内容
                        for (int i = 0; i < personInfos.size(); i++) {
                            row = sheet.createRow(i + 1);
                            row.setHeightInPoints(80);
                            PersonInfo personInfo = personInfos.get(i);
                            //照片存放路径
//                                String photo = "D:\\study\\金现代\\冠县社保信息照片\\"+ssPersonInfo.getIdentityCard()+".JPG";
//                                BufferedImage image = ImageIO.read(new FileInputStream(photo));

                            // anchor主要用于设置图片的属性
                            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 13, i+1, (short) 13, i+1);
                            BufferedImage bufferImg = null;
                            // 先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray

                            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                            String imagePath = photoPath+personInfo.getIdentityCard()+".jpg";
                            File imageFile = new File(imagePath);


                            cell = row.createCell(0);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getIdentityNumber());
                            cell = row.createCell(1);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getIdentityCard());
                            cell = row.createCell(2);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getName());
                            cell = row.createCell(3);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getSex());
                            cell = row.createCell(4);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getNation());
                            cell = row.createCell(5);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getBirthday());
                            cell = row.createCell(6);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getPostcode());
                            cell = row.createCell(7);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getAddress());
                            cell = row.createCell(8);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getPhone());
                            cell = row.createCell(9);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getNativePlace());
                            cell = row.createCell(10);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getTown());
                            cell = row.createCell(11);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getVillage());
                            cell = row.createCell(12);
                            cell.setCellStyle(cs);
                            cell.setCellValue(personInfo.getAgency());
                            cell = row.createCell(13);
                            // 插入图片
                            if (imageFile.exists()){
                                bufferImg = ImageIO.read(imageFile);
                                if(bufferImg!=null){
                                    ImageIO.write(bufferImg, "png", byteArrayOut);
                                    patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
                                }
                            }

                        }
                        wb.write(os);
                        InputStream excelStream = new ByteArrayInputStream(os.toByteArray());
                        //写入目标文件
                        byte[] buffer = new byte[1024*1024];
                        int byteRead = 0;
                        while((byteRead= excelStream.read(buffer))!=-1){
                            fos.write(buffer, 0, byteRead);
                            fos.flush();
                        }
                        fos.close();
                        excelStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        long endTime;
        executorService.shutdown();
        while(true){
            if(executorService.isTerminated()){
                endTime = System.currentTimeMillis();
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //导出总耗时
        long time = endTime-startTime;
        System.out.println("======================导出结束,耗时:"+(endTime-startTime)/1000+"s");
        result = "导出成功,耗时:"+time/1000+"s!!!";
        return result;

    }

    /**
     * 取出相应照片
     */
    //取出配置信息中的路径
    @Value("${fileSource}")
    private String fileSource;
    @Value("${fileDest}")
    private String fileDest;
    @Override
    public Map<String, Object> mobilePhotos()throws IOException {
        ExecutorService executorService = new ThreadPoolExecutor(50,100,0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(150),
                new ThreadPoolExecutor.AbortPolicy());

        Map<String, Object> map = new HashMap<>();
        //开始时间
        long startTime = System.currentTimeMillis();
        //身份证号list
        List<String> list = personInfoMapper.mobilePhotos();
        //一个线程的条数
        //        double cou = 1000;
        //        //线程数
        //int count = (int) Math.ceil(list.size()/cou);
//        for (int i = 0;i < count;i++) {
//            List<String> listTemp;
//            if(i==count-1){
//                listTemp = new ArrayList<String>(list.subList(i*count,list.size()));
//            }else{
//                listTemp = new ArrayList<String>(list.subList(i*count,(i+1)*count));
//            }
//            executorService.execute(()->{
                for (String id : list) {
                    //照片源路径
                    File source = new File(fileSource + id +".jpg");
                    if (!source.exists()){
                        continue;
                    }
                    //照片目的路径
                    File dest = new File(fileDest + id +".jpg");
                    FileChannel inputChannel = null;
                    FileChannel outputChannel = null;
                    try {
                        inputChannel = new FileInputStream(source).getChannel();
                        outputChannel = new FileOutputStream(dest).getChannel();
                        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            inputChannel.close();
                            outputChannel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
//            });
//        }
        //结束时间
        long endTime;
//        executorService.shutdown();
//        while(true){
//            if(executorService.isTerminated()){
//                endTime = System.currentTimeMillis();
//                break;
//            }
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        //耗时
        endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        System.out.println("======================耗时:"+time/1000+"s");
        map.put("message","导出成功!");
        map.put("time",time/1000+"s");
        return map;
    }
    /**
     * 用于导出所有有照片的数据
     * @param response 用于输出文件
     * @param request 请求头
     * @return 所有有照片的人员
     */
    @Override
    public String havePhoto(HttpServletResponse response, HttpServletRequest request) {
        int rowNum = 1;
        String result;
        List<Map<String, Object>> allTowns = personInfoMapper.getAllTown();
        String dirPath = excelPath;
        long startTime = System.currentTimeMillis();
         List<PersonInfo> personInfos = personInfoMapper.getAllPersonInfo();
        String fileName = "已有照片信息的社保人员清单.xls";
        String path = dirPath+"\\"+"已有照片信息"+"\\";
        File targetFile = new File(path);
        if(!targetFile.exists()){//如果文件夹不存在
            targetFile.mkdirs();
        }
        //response.setContentType("octets/stream");
        try {
            FileOutputStream fos = new FileOutputStream(new File(path+fileName));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            HSSFWorkbook wb = new HSSFWorkbook();
            String sheetName="社保人员清单";
            HSSFSheet sheet = wb.createSheet(sheetName);
            HSSFCellStyle cs = wb.createCellStyle();
            cs.setAlignment(HorizontalAlignment.CENTER);
            cs.setVerticalAlignment(VerticalAlignment.CENTER);
            // 画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

            /* 设置打印格式 */
            HSSFPrintSetup hps = sheet.getPrintSetup();
            hps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
            hps.setLandscape(true);
            hps.setFitHeight((short) 1);
            hps.setFitWidth((short) 1);
            hps.setScale((short) 65);
            hps.setFooterMargin(0);
            hps.setHeaderMargin(0);
            sheet.setMargin(HSSFSheet.TopMargin, 0.3);
            sheet.setMargin(HSSFSheet.BottomMargin, 0);
            sheet.setMargin(HSSFSheet.LeftMargin, 0.3);
            sheet.setMargin(HSSFSheet.RightMargin, 0);


            //创建第一行
            HSSFRow row = sheet.createRow((short) 0);
            HSSFCell cell ;
            row.setHeightInPoints(20);

            HSSFFont font = wb.createFont();
            font.setFontName("宋体");
            //粗体显示
//                        font.setBold(true);
            font.setFontHeightInPoints((short) 12);
            cs.setFont(font);
            cell = row.createCell(0);
            cell.setCellStyle(cs);
            cell.setCellValue("人员标识（不可修改）");
            cell = row.createCell(1);
            cell.setCellStyle(cs);
            cell.setCellValue("身份证号码（不可修改）");
            cell = row.createCell(2);
            cell.setCellStyle(cs);
            cell.setCellValue("姓名（不可修改）");
            cell = row.createCell(3);
            cell.setCellStyle(cs);
            cell.setCellValue("性别（必填）");
            cell = row.createCell(4);
            cell.setCellStyle(cs);
            cell.setCellValue("民族（必填）");
            cell = row.createCell(5);
            cell.setCellStyle(cs);
            cell.setCellValue("出生日期（必填）");
            cell = row.createCell(6);
            cell.setCellStyle(cs);
            cell.setCellValue("邮政编码（必填）");
            cell = row.createCell(7);
            cell.setCellStyle(cs);
            cell.setCellValue("通讯地址（必填）");
            cell = row.createCell(8);
            cell.setCellStyle(cs);
            cell.setCellValue("手机号码（必填）");
            cell = row.createCell(9);
            cell.setCellStyle(cs);
            cell.setCellValue("村镇社区/学校");
            cell = row.createCell(10);
            cell.setCellStyle(cs);
            cell.setCellValue("所属镇");
            cell = row.createCell(11);
            cell.setCellStyle(cs);
            cell.setCellValue("所属村");
            cell = row.createCell(12);
            cell.setCellStyle(cs);
            cell.setCellValue("经办机构名称");
            cell = row.createCell(13);
            cell.setCellStyle(cs);
            cell.setCellValue("照片");
            cell.setCellStyle(cs);
            sheet.setColumnWidth(0, 7168);
            sheet.setColumnWidth(1, 7168);
            sheet.setColumnWidth(2, 5120);
            sheet.setColumnWidth(3, 5120);
            sheet.setColumnWidth(4, 5120);
            sheet.setColumnWidth(5, 5120);
            sheet.setColumnWidth(6, 5120);
            sheet.setColumnWidth(7, 5120);
            sheet.setColumnWidth(8, 5120);
            sheet.setColumnWidth(9, 6124);
            sheet.setColumnWidth(10, 4096);
            sheet.setColumnWidth(11, 4096);
            sheet.setColumnWidth(12, 5120);
            //设置列值-内容
            for (int i = 0; i < personInfos.size(); i++) {
                PersonInfo personInfo = personInfos.get(i);
                //照片存放路径
//                                String photo = "D:\\study\\金现代\\冠县社保信息照片\\"+ssPersonInfo.getIdentityCard()+".JPG";
//                                BufferedImage image = ImageIO.read(new FileInputStream(photo));

                // anchor主要用于设置图片的属性
                BufferedImage bufferImg = null;
                // 先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray

                ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                String imagePath = photoPath+personInfo.getIdentityCard()+".jpg";
                File imageFile = new File(imagePath);
                // 插入图片
                if (imageFile.exists()){
                    HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 13, rowNum+1, (short) 13, rowNum+1);
                    row = sheet.createRow((rowNum));
                    row.setHeightInPoints(80);
                    cell = row.createCell(0);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getIdentityNumber());
                    cell = row.createCell(1);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getIdentityCard());
                    cell = row.createCell(2);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getName());
                    cell = row.createCell(3);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getSex());
                    cell = row.createCell(4);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getNation());
                    cell = row.createCell(5);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getBirthday());
                    cell = row.createCell(6);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getPostcode());
                    cell = row.createCell(7);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getAddress());
                    cell = row.createCell(8);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getPhone());
                    cell = row.createCell(9);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getNativePlace());
                    cell = row.createCell(10);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getTown());
                    cell = row.createCell(11);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getVillage());
                    cell = row.createCell(12);
                    cell.setCellStyle(cs);
                    cell.setCellValue(personInfo.getAgency());
                    cell = row.createCell(13);
//                        ImageIO.write(bufferImg, "png", byteArrayOut);
//                        patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
                    rowNum++;
                    personInfoMapper.updateIsUsed(personInfo.getIdentityCard());
//                    bufferImg = ImageIO.read(imageFile);
//                    if(bufferImg!=null){
//
//                    }
                }

            }
            wb.write(os);
            InputStream excelStream = new ByteArrayInputStream(os.toByteArray());
            //写入目标文件
            byte[] buffer = new byte[1024*1024];
            int byteRead = 0;
            while((byteRead= excelStream.read(buffer))!=-1){
                fos.write(buffer, 0, byteRead);
                fos.flush();
            }
            fos.close();
            excelStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();


        //导出总耗时
        long time = endTime-startTime;
        System.out.println("======================导出结束,耗时:"+(endTime-startTime)/1000+"s");
        result = "导出成功,耗时:"+time/1000+"s!!!";
        return result;
    }

    @Override
    public int delPersonInfo(String identityId) {
        return personInfoMapper.delPersonInfo(identityId);
    }
}
