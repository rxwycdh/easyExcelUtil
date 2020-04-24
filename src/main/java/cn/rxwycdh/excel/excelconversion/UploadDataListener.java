package cn.rxwycdh.excel.excelconversion;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static cn.rxwycdh.excel.excelconversion.DbQueryUtil.*;

public class UploadDataListener extends AnalysisEventListener<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadDataListener.class);

    private static final int BATCH_COUNT = 5;

    private Object mapperObject;

    private Object entityObject;

    private String type;

    private Method insertSelectiveMethod;

    private Method updateByPrimaryKeySelectiveMethod;

    private Method getIdMethod;

    private Method setGmtModifiedMethod;

    private Method setGmtCreateMethod;

    List<Object> list = new ArrayList<>();

    public UploadDataListener(Class<?> entityClass, Class<?> mapperClass, String type, ReflectUtil reflectUtil)
            throws Exception {

        this.entityObject = entityClass.getDeclaredConstructor().newInstance();
        this.mapperObject = reflectUtil.getMapperProxyObject(mapperClass);
        this.type = type;

        this.insertSelectiveMethod = mapperObject.getClass().getMethod(INSERT_METHOD, entityClass);
        this.updateByPrimaryKeySelectiveMethod = mapperObject.getClass().getMethod(UPDATE_METHOD, entityClass);
        this.getIdMethod = entityClass.getMethod(GETID_METHOD);
        this.setGmtModifiedMethod = entityClass.getMethod(SETGMTMODIFIED_METHOD, Date.class);
        this.setGmtCreateMethod = entityClass.getMethod(SETGMTCREATE_METHOD, Date.class);
    }

    @Override
    public void invoke(Object data, AnalysisContext context) {
        LOGGER.debug("解析到一条数据" + JSON.toJSONString(data));
        list.add(data);
        if (list.size() >= BATCH_COUNT) {
            saveData(type);
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData(type);
        LOGGER.info("全部解析完成");
    }

    private void saveData(String type){
        if(UPDATE.equals(type)) {
            list.forEach(item -> {
                try {
                    Objects.requireNonNull(getIdMethod.invoke(item),"update Id can not be null!");
                    setGmtModifiedMethod.invoke(item, new Date());
                    LOGGER.debug("更新id为" + getIdMethod.invoke(item) + "的数据:" + item);
                    updateByPrimaryKeySelectiveMethod.invoke(mapperObject, item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }else if(INSERT.equals(type)) {
            list.forEach(item -> {
                try {
                    // 因为主键有两种类型，所以插入的时候统一不要填写id
                    ReflectUtil.requireNull(getIdMethod.invoke(item),"Id must be null!");

                    setGmtModifiedMethod.invoke(item, new Date());
                    setGmtCreateMethod.invoke(item, new Date());
                    LOGGER.debug("插入:" + item);

                    insertSelectiveMethod.invoke(mapperObject, item);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
