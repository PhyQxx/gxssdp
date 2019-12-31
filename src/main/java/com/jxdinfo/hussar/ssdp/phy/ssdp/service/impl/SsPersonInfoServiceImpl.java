package com.jxdinfo.hussar.ssdp.phy.ssdp.service.impl;

import com.jxdinfo.hussar.ssdp.phy.ssdp.model.SsPersonInfo;
import com.jxdinfo.hussar.ssdp.phy.ssdp.dao.SsPersonInfoMapper;
import com.jxdinfo.hussar.ssdp.phy.ssdp.service.ISsPersonInfoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.util.ExcelSaxReader;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.RichTextString;
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
import java.util.concurrent.*;

/**
 * <p>
 * 社保人员信息表 服务实现类
 * </p>
 *
 * @author 裴浩宇
 * @since 2019-10-08
 */
@Service
public class SsPersonInfoServiceImpl extends ServiceImpl<SsPersonInfoMapper, SsPersonInfo> implements ISsPersonInfoService {

    @Autowired
    SsPersonInfoMapper ssPersonInfoMapper;
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
                List<SsPersonInfo> personInfos = new ArrayList<>();
                long startTime = System.currentTimeMillis();
                //转换为输入流
                inputStream = file.getInputStream();
                ExcelSaxReader reader = excelSaxReader.parse(inputStream);
                List<String[]> datas = reader.getDatas();
                for(String[] str : datas){
                    SsPersonInfo personInfo = new SsPersonInfo();
                    personInfo.setIdentityCard(str[1]);
                    personInfo.setName(str[2]);
                    personInfo.setNativePlace(str[17]);
                    /**
                     * 根据籍贯截取出镇
                     */
                    int index = str[17].indexOf(" ");
                    int lastIndexOf = str[17].lastIndexOf(" ");
                    String town = str[17].substring(index, lastIndexOf);
                    personInfo.setTown(town);
                    /**
                     * 根据籍贯截取出村
                     */
                    String village = str[17].substring(lastIndexOf);
                    personInfo.setVillage(village);
                    personInfos.add(personInfo);
                }
                long endTime = System.currentTimeMillis();
                //读取Excel耗时            =============45s左右
                long time = endTime-startTime;
                long startTime2 = System.currentTimeMillis();
                resultMap[0] = ssPersonInfoMapper.batchInsertAll(personInfos);
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
        return ssPersonInfoMapper.getAllTown();
    }

    /**
     * 根据乡镇获取所辖的村/社区
     * @param map 乡镇
     * @return 本镇所有的村
     */
    @Override
    public List<Map<String, Object>> getAllVillage(Map<String, Object> map) {
        return ssPersonInfoMapper.getAllVillage(map);
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
    public String VillageFile(HttpServletResponse response, HttpServletRequest request) {
        String result;
        List<Map<String, Object>> allTowns = ssPersonInfoMapper.getAllTown();
//        String dirPath = request.getSession().getServletContext().getRealPath("/static/download/");
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
                List<Map<String, Object>> allVillages = ssPersonInfoMapper.getAllVillage(town);
                for (Map<String, Object> village : allVillages){
                    Map<String,Object> param = new HashMap<>(5);
                    String Village = (String) village.get("Village");

                    param.put("Town",Town);
                    param.put("Village",Village);
                    //填入Excel表格中的数据
                    List<SsPersonInfo> ssPersonInfos = ssPersonInfoMapper.getPayableInfoByVillage(param);
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
                        /*response.addHeader("Content-Disposition",
                                "attachment;filename=" + new String(fileName.getBytes("GB2312"), "ISO8859-1"));*/
                        HSSFWorkbook wb = new HSSFWorkbook();
                        String sheetName=Village+"社保人员清单";
                        HSSFSheet sheet = wb.createSheet(sheetName);

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
                        row.setHeightInPoints(40);

                        HSSFFont font = wb.createFont();
                        font.setFontName("宋体");
                        //粗体显示
                        font.setBold(true);
                        font.setFontHeightInPoints((short) 16);
                        cell = row.createCell(0);
                        cell.setCellValue("身份证");
                        cell = row.createCell(1);
                        cell.setCellValue("姓名");
                        cell = row.createCell(2);
                        cell.setCellValue("籍贯");
                        cell = row.createCell(3);
                        cell.setCellValue("镇");
                        cell = row.createCell(4);
                        cell.setCellValue("村");
                        cell = row.createCell(5);
                        cell.setCellValue("照片");
                        sheet.setColumnWidth(0, 4096);
                        sheet.setColumnWidth(1, 4096);
                        sheet.setColumnWidth(2, 4096);
                        sheet.setColumnWidth(3, 4096);
                        sheet.setColumnWidth(4, 4096);
                        sheet.setColumnWidth(5, 3000);
                        //设置列值-内容
                        for (int i = 0; i < ssPersonInfos.size(); i++) {
                            row = sheet.createRow(i + 1);
                            row.setHeightInPoints(80);
                            SsPersonInfo ssPersonInfo = ssPersonInfos.get(i);
                            //照片存放路径
//                                String photo = "D:\\study\\金现代\\冠县社保信息照片\\"+ssPersonInfo.getIdentityCard()+".JPG";
//                                BufferedImage image = ImageIO.read(new FileInputStream(photo));

                            // anchor主要用于设置图片的属性
                            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 5, i+1, (short) 5, i+1);
                            BufferedImage bufferImg = null;
                            // 先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray

                            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                            String imagePath = photoPath+ssPersonInfo.getIdentityCard()+".jpg";
                            File imageFile = new File(imagePath);
//                            if (imageFile.exists()){
//                                bufferImg = ImageIO.read(new File(imagePath));
//                                ImageIO.write(bufferImg, "png", byteArrayOut);
//                            }
//                            bufferImg = ImageIO.read(new File(imagePath));
//                            bufferImg = ImageIO.read(new File("D:\\study\\金现代\\冠县社保信息照片\\000525200305255924.jpg"));



                            cell = row.createCell(0);
                            cell.setCellValue(ssPersonInfo.getIdentityCard());
                            cell = row.createCell(1);
                            cell.setCellValue(ssPersonInfo.getName());
                            cell = row.createCell(2);
                            cell.setCellValue(ssPersonInfo.getNativePlace());
                            cell = row.createCell(3);
                            cell.setCellValue(ssPersonInfo.getTown());
                            cell = row.createCell(4);
                            cell.setCellValue(ssPersonInfo.getVillage());
                            cell = row.createCell(5);

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
        List<String> list = ssPersonInfoMapper.mobilePhotos();
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


}
