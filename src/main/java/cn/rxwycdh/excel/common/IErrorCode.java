package cn.rxwycdh.excel.common;

/**
 * @FileName IErrorCode
 * @Description
 * @Author jiuhao
 * @Date 2020/4/24 14:46
 * @Modified
 * @Version 1.0
 */
public interface IErrorCode {
    /**
     * 获取结果码
     *
     * @return code
     */
    long getCode();

    /**
     * 返回结果信息
     *
     * @return message
     */
    String getMessage();
}
