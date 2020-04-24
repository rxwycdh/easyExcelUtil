package cn.rxwycdh.excel.excelconversion;


import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.function.Supplier;

@Component
public class ReflectUtil {

    @Autowired
    SqlSession sqlSession;

    public final static String ENTITY_PATH_PREFIX = "com.bkit.fatdown.entity.";

    public final static String MAPPER_PATH_PREFIX = "com.bkit.fatdown.mappers.";

    public final static String EXAMPLE_PATH_PREFIX = "com.bkit.fatdown.entity.";



    public static Class<?> getClass(Supplier<String> referenceSupplier) throws ClassNotFoundException {
        return Class.forName(referenceSupplier.get());
    }

    public static Object getObject(Class<?> t) throws Exception {
        return t.getDeclaredConstructor().newInstance();
    }

    public Object getMapperProxyObject(Class<?> mapperClass) {
        return Proxy.newProxyInstance(mapperClass.getClassLoader(),
                new Class[]{mapperClass},
                new MapperInvocationHandler(sqlSession.getMapper(mapperClass)));
    }

    public static <T> T castObject(Object o, Class<T> clazz){
        Objects.requireNonNull(clazz);
        if(clazz.isInstance(o)) {
                return clazz.cast(o);
        }else {
                throw new RuntimeException(o + " is not a " + clazz.getName());
        }
    }

    public static <T> T requireNull(T obj, String message) {
        if (obj != null) {
            throw new IllegalArgumentException(message);
        }
        return null;
    }

}
