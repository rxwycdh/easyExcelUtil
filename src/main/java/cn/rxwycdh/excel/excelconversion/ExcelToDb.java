package cn.rxwycdh.excel.excelconversion;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ExcelToDb {
    @Autowired
    ExcelWebTransferUtil excelWebTransferUtil;

    /**
     * @param file
     * @param tableName
     * @param type update 或者 insert
     * @throws Exception
     */
    public void startExportInsertOrUpdateToDb(MultipartFile file, String tableName, String type) throws Exception {
        String entityName = DbQueryUtil.tableNameToEntityName(tableName);
        excelWebTransferUtil.read(file, entityName, type);
    }
}
