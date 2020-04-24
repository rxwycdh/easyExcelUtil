package cn.rxwycdh.excel.excelconversion;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 一个用于获取Mybatis的Mapper接口的动态代理实现
 */
public class MapperInvocationHandler implements InvocationHandler {

    private Object target;

    public MapperInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(target,args);
    }

}
