package cn.rxwycdh.excel.excelconversion;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.rxwycdh.excel.excelconversion.ReflectUtil.ENTITY_PATH_PREFIX;
import static cn.rxwycdh.excel.excelconversion.ReflectUtil.MAPPER_PATH_PREFIX;


@Component
public class ExcelWebTransferUtil {

    @Autowired
    private ReflectUtil reflectUtil;

    public void download(String fileName, Class<?> entityClass, List<?> dataList
            , List<List<String>> annatationList, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
//        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), entityClass)
                .sheet("模板").head(annatationList).doWrite(dataList);
        System.out.println(entityClass.getDeclaredFields().length);
    }

    public void download(String entityName, List<?> dataList
            , List<List<String>> annatationList, HttpServletResponse response) throws IOException, ClassNotFoundException {
        Class<?> entityClass = ReflectUtil.getClass(() -> ENTITY_PATH_PREFIX + entityName);
        download(entityName, entityClass, dataList, annatationList, response);
    }

    public void download(String entityName, List<List<String>> annatationList, HttpServletResponse response) throws IOException, ClassNotFoundException {
        Class<?> entityClass = ReflectUtil.getClass(() -> ENTITY_PATH_PREFIX + entityName);
        download(entityName, entityClass, Collections.emptyList(), annatationList, response);
    }

    public void read(MultipartFile file, String entityName, String type) throws Exception {
        Class<?> entityClass = ReflectUtil.getClass(() -> ENTITY_PATH_PREFIX + entityName);
        Class<?> mapperClass = ReflectUtil.getClass(() -> MAPPER_PATH_PREFIX + entityName + "Mapper");
        EasyExcel.read(file.getInputStream(), entityClass,
                new UploadDataListener(entityClass, mapperClass, type, reflectUtil)).sheet().doRead();
    }

    public void failDownload(HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        Map<String, String> map = new HashMap<>(16);
        map.put("status", "failure");
        map.put("message", "下载文件失败");
        response.getWriter().println(JSON.toJSONString(map));
    }


}
