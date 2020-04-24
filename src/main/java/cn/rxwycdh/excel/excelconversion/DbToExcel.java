package cn.rxwycdh.excel.excelconversion;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static cn.rxwycdh.excel.excelconversion.ReflectUtil.ENTITY_PATH_PREFIX;

@Component
public class DbToExcel {

    @Autowired
    DbQueryUtil dbQueryUtil;

    @Autowired
    ExcelWebTransferUtil excelWebTransferUtil;


    /**
     *
     * @param tableName 数据表名字
     * @param start
     * @param end
     * @param response
     * @throws Exception
     */
    public void startExportToExcel(String tableName, Integer start, Integer end, HttpServletResponse response) throws Exception {
        String entityName = DbQueryUtil.tableNameToEntityName(tableName);

        List<?> queryResult = dbQueryUtil.startToQuery(entityName);
        // 截取结果
        queryResult = DbQueryUtil.sliceList(queryResult, start, end);
        // 获取实体类属性注解作为自定义标题头
        List<List<String>> headList = dbQueryUtil.listEntityAnnotation(entityName);

        excelWebTransferUtil.download(entityName, queryResult, headList, response);
    }

    /**
     * 下载一个空的模板Excxel
     * @param tableName
     * @param response
     * @throws Exception
     */
    public void startExportToExcel(String tableName, HttpServletResponse response) throws Exception {

        String entityName = DbQueryUtil.tableNameToEntityName(tableName);
        // 获取对应实体类T
        Class<?> entityClass = ReflectUtil.getClass(() -> ENTITY_PATH_PREFIX + entityName);
        // 获取自定义标题头
        List<List<String>> headList = dbQueryUtil.listEntityAnnotation(entityClass);

        excelWebTransferUtil.download(entityName, headList, response);
    }


}
