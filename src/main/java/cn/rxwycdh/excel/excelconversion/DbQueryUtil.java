package cn.rxwycdh.excel.excelconversion;


import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.rxwycdh.excel.excelconversion.ReflectUtil .*;

@Component
public class DbQueryUtil {
    public final static String UPDATE = "update";

    public final static String INSERT = "insert";

    public final static String GETID_METHOD = "getId";

    public final static String SETGMTMODIFIED_METHOD = "setGmtModified";

    public final static String SETGMTCREATE_METHOD = "setGmtCreate";

    public final static String QUERY_METHOD = "selectByExample";

    public final static String INSERT_METHOD = "insertSelective";

    public final static String UPDATE_METHOD = "updateByPrimaryKeySelective";

    public final static String SELECTBYPRIMARYKEY_METHOD = "selectByPrimaryKey";



    @Autowired
    private ReflectUtil reflectUtil;

    public List<?> listQueryResult(Class<?> exampleClass, Class<?> mapperClass) throws Exception {


        Object exampleObject = ReflectUtil.getObject(exampleClass);
        Object mapperObject = reflectUtil.getMapperProxyObject(mapperClass);

        Method selectByExample = mapperObject.getClass().getMethod(QUERY_METHOD, exampleClass);

        return (List<?>)selectByExample.invoke(mapperObject, exampleObject);

    }


    /**
     * 按照注释在实体类属性上的{@link ApiModelProperty}来excel自定义标题头 如果没有注解则用属性名字
     * @param entityClass
     * @return 用于EasyExcel写excel的标题头列表
     */
    public List<List<String>> listEntityAnnotation(Class<?> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        List<List<String>> titleList = new ArrayList<>();
        for (Field field : fields) {
            List<String> titleElement = new ArrayList<>();
            ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
            if(annotation == null ) {
                if("serialVersionUID".equals(field.getName())) {
                    continue;
                }
                titleElement.add(field.getName());
            }else {
                titleElement.add(annotation.value());
            }
            titleList.add(titleElement);
        }
        return titleList;
    }

    public List<List<String>> listEntityAnnotation(String entityName) throws ClassNotFoundException {
        return listEntityAnnotation(ReflectUtil.getClass(() -> ENTITY_PATH_PREFIX + entityName));
    }

    /**
     * 分割查询到的结果List
     * @param list
     * @param start
     * @param end
     * @return
     */
    public static List<?> sliceList(List<?> list, Integer start, Integer end) {
        List<?> resultList;
        if(start == -1 && end != -1) {
            // 截取前end个
            resultList = list.stream().limit(end).collect(Collectors.toList());
        }else if(start != -1 && end == -1) {
            // 跳过前start个
            resultList = list.stream().skip(start).collect(Collectors.toList());
        }else if(start != -1) {
            // 截取[start, end]区间
            resultList = list.subList(start, end);
        }else {
            // 不截取
            return list;
        }
        return resultList;
    }

    /**
     * 将数据表名字转为对应的实体类名字
     * @param tableName
     * @return
     */
    public static String tableNameToEntityName(String tableName) {
        String[] split = tableName.split("_");
        List<String> list = Arrays.asList(split);
        return list.stream().map(str -> str.substring(0,1).toUpperCase() + str.substring(1))
                .collect(Collectors.joining());
    }

    public List<?> startToQuery(String entityName) throws Exception {
        // 获取对应实体类T
        Class<?> entityClass = ReflectUtil.getClass(() -> ENTITY_PATH_PREFIX + entityName);
        // 获取Mapper Class
        Class<?> mapperClass = ReflectUtil.getClass(() -> MAPPER_PATH_PREFIX + entityName + "Mapper");
        // 获取实体类Example Class
        Class<?> entityExampleClass = ReflectUtil.getClass(() -> EXAMPLE_PATH_PREFIX + entityName + "Example");
        // 获取查询结果
        return listQueryResult(entityExampleClass, mapperClass);
    }


}
