package cn.rxwycdh.excel.controller;

import cn.rxwycdh.excel.common.CommonResult;
import cn.rxwycdh.excel.excelconversion.DbQueryUtil;
import cn.rxwycdh.excel.excelconversion.DbToExcel;
import cn.rxwycdh.excel.excelconversion.ExcelToDb;
import cn.rxwycdh.excel.excelconversion.ExcelWebTransferUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @FileName ExcelController
 * @Description
 * @Author jiuhao
 * @Date 2020/4/24 14:41
 * @Modified
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/excelConvert")
public class ExcelController {

    @Autowired
    DbQueryUtil dbQueryUtil;

    @Autowired
    ExcelWebTransferUtil excelWebTransferUtil;

    @Autowired
    DbToExcel dbToExcel;

    @Autowired
    ExcelToDb excelToDb;

    @ApiOperation(value = "导出数据表到Excel", notes = "传入 数据表名（,start,end）" +
            "比如end=10代表保留前十个 start=10代表跳过前十个 start=10&end=20代表取第20到30个 默认返回完整数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableName", value = "数据表名字", paramType = "query", dataType = "String",
                    required = true),
            @ApiImplicitParam(name = "start", value = "从哪个位置开始", paramType = "query", dataType = "Integer"
            ),
            @ApiImplicitParam(name = "end", value = "从哪个位置结束", paramType = "query", dataType = "Integer"
            )
    })
    @RequestMapping(value = "/downloadDbData", method = RequestMethod.GET)
    public void downloadDbData(@RequestParam("tableName")String tableName,
                               @RequestParam(value = "start",defaultValue = "-1")Integer start,
                               @RequestParam(value = "end",defaultValue = "-1")Integer end,
                               HttpServletResponse response) throws IOException {
        try {
            dbToExcel.startExportToExcel(tableName, start, end, response);
        }catch (Exception e) {
            excelWebTransferUtil.failDownload(response);
        }
    }

    @ApiOperation(value = "上传Excel保存到数据库", notes = "传入 文件，数据表名，类型（如果是insert,需要第一列为空（即id为空）" +
            ",如果是update,需要第一列不为空(即需要指明id)）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "Excel文件", paramType = "query", dataType = "String",
                    required = true),
            @ApiImplicitParam(name = "tableName", value = "数据表名字", paramType = "query", dataType = "Integer",
                    required = true),
            @ApiImplicitParam(name = "type", value = "类型：insert 或者 update", paramType = "query", dataType = "Integer",
                    required = true)
    })
    @RequestMapping(value = "/uploadExcelSaveToDb", method = RequestMethod.GET)
    public CommonResult uploadExcelSaveToDb(@RequestParam("file") MultipartFile file,
                                            @RequestParam("tableName")String tableName,
                                            @RequestParam("type")String type)  {
        try {
            excelToDb.startExportInsertOrUpdateToDb(file, tableName, type);
            return CommonResult.success();

        } catch (Exception e) {
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "下载一个空的模板", notes = "传入数据表名 如果不是用这个模板来进行upload的话 " +
            "则原始excel第一行要留空（标题），第二行开始从左到右要符合实体类的属性声明顺序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableName", value = "数据表名字", paramType = "query", dataType = "String",
                    required = true)
    })
    @RequestMapping(value = "/downloadTemplateExcel", method = RequestMethod.GET)
    public void downloadTemplateExcel(@RequestParam("tableName")String tableName,
                                      HttpServletResponse response) throws IOException {
        try {
            dbToExcel.startExportToExcel(tableName, response);

        }catch (Exception e) {
            excelWebTransferUtil.failDownload(response);
        }
    }
}