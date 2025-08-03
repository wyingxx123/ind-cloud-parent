package com.dfc.ind.service.impl;

import com.dfc.ind.common.core.constant.Constants;
import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.LoadExcelInfoEntity;

import com.dfc.ind.mapper.LoadExcelInfoMapper;
import com.dfc.ind.service.ILoadExcelInfoService;
import com.dfc.ind.service.IPubDictDataService;

import com.dfc.ind.vo.PubDictDataVo;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 导入模板信息 服务实现类
 * </p>
 *
 * @author huff
 * @since 2024-07-22
 */
@Slf4j
@Service
public class LoadExcelInfoServiceImpl extends MppServiceImpl<LoadExcelInfoMapper, LoadExcelInfoEntity> implements ILoadExcelInfoService {


    @Autowired
    private IPubDictDataService dictDataService;
    /**
     * 一次最大执行导入数量
     */
    private final Integer maxCount=1000;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonResults importData(MultipartFile file, LoadExcelInfoEntity entity) {
        Long merchantId =SecurityUtils.getLoginUser().getMerchantId();
        String userName = SecurityUtils.getUserName();
        String appId = entity.getAppId();
        entity.setMerchantId(merchantId);
        LoadExcelInfoEntity infoEntity = this.selectByMultiId(entity);
        if (infoEntity==null){
            throw new CustomException("查询不到模板信息");
        }
        String filename = file.getOriginalFilename();
        //TODO 查询字典数据
        setDictData(infoEntity, merchantId,entity.getTemplateNo());
        PubDictDataVo destTableName = getByCodeName(infoEntity.getFormatDictTypeList(), "destTableName");
        PubDictDataVo destSchema = getByCodeName(infoEntity.getFormatDictTypeList(), "destSchema");
        if (destSchema==null||StringUtils.isEmpty(destSchema.getCodeValue())){
            throw new CustomException("导入的目标库不能为空");
        }
        PubDictDataVo endFlag = getByCodeName(infoEntity.getFormatDictTypeList(), "endFlag");

        //TODO 清除指定日期数据
        if (StringUtils.isNotEmpty(entity.getClearStartDate())&&StringUtils.isNotEmpty(entity.getClearEndDate())){
            String clearSql="delete from "+"\\`"+destSchema.getCodeValue()+"\\`."+destTableName.getCodeValue()+" where excel_tmpl_no='"+entity.getTemplateNo()+"' and import_date between '"+entity.getClearStartDate()
                    +"' and "+"'"+entity.getClearEndDate()+"'";
            try {
                jdbcTemplate.execute(clearSql);
            }catch (Exception e){
                log.error("清除指定日期数据失败:",e);
                throw new CustomException("清除指定日期数据失败");
            }

        }
        try {
            String ext = filename.substring(filename.lastIndexOf("."));
            Workbook workbook ;
            if(".xls".equals(ext)||".XLS".equals(ext)){
                workbook = new HSSFWorkbook(file.getInputStream());
            }else if(".xlsx".equals(ext)||".XLSX".equals(ext)){
                workbook = new XSSFWorkbook(file.getInputStream());
            }else {
                throw new CustomException("不支持的文件格式");
            }
            if (entity.getSheetAt()==null||entity.getSheetAt()<=0){
                entity.setSheetAt(1);
            }
            Sheet sheet = workbook.getSheetAt(entity.getSheetAt()-1);
            //开始行默认1
            int startRowNum =1;
            int lastRowNum = sheet.getLastRowNum();

            //数据开始行号
            PubDictDataVo dataStartRowNo = getByCodeName(infoEntity.getFormatDictTypeList(), "dataStartRowNo");
            if (dataStartRowNo!=null&&StringUtils.isNotEmpty(dataStartRowNo.getCodeValue())){
                startRowNum= Integer.parseInt(dataStartRowNo.getCodeValue())-1;
            }

            //数据结束行号
            PubDictDataVo dataEndRowNo = getByCodeName(infoEntity.getFormatDictTypeList(), "dataEndRowNo");
            if (dataEndRowNo!=null&&StringUtils.isNotEmpty(dataEndRowNo.getCodeValue())){
                String dataEndRowStr = dataEndRowNo.getCodeValue();
                //结束行
                lastRowNum = Integer.parseInt(dataEndRowStr)-1;
            }

            //表头是否检查
            PubDictDataVo isCheckHead = getByCodeName(infoEntity.getFormatDictTypeList(), "isCheckHead");
            //TODO  表头校验
            checkHead(infoEntity, sheet, isCheckHead);

            List<PubDictDataVo> headCheckList = infoEntity.getDataDictTypeList();

            //TODO  获取sqllie
            StringBuffer stringBuffer=new StringBuffer("replace into "+"`"+destSchema.getCodeValue()+"`."+destTableName.getCodeValue()+"(");
            for (PubDictDataVo pubDictDataVo : headCheckList) {
                if (Constants.Y.equals(pubDictDataVo.getIsDisplay())){
                    String filedName = pubDictDataVo.getCodeName();
                    stringBuffer.append(filedName).append(",");
                }
            }
            if (!CollectionUtils.isEmpty(infoEntity.getParaDictTypeList())){
                for (PubDictDataVo pubDictDataVo : infoEntity.getParaDictTypeList()) {
                    if (Constants.Y.equals(pubDictDataVo.getIsDisplay())){
                        String filedName = pubDictDataVo.getCodeName();
                        stringBuffer.append(filedName).append(",");
                    }
                }
            }
            String sql=stringBuffer.toString();
            sql=sql.substring(0,sql.length()-1)+") VALUES ";
            StringBuffer sqlSb=new StringBuffer();

            Integer count=0;

           A: for (int i = startRowNum; i <= lastRowNum; i++) {
               StringBuilder sqlAdd=new StringBuilder();
                Row row = sheet.getRow(i);
                if (row==null){
                    break ;
                }
                sqlAdd.append("(");

               for (PubDictDataVo pubDictDataVo : headCheckList) {
                   int cellIndex = Integer.parseInt(pubDictDataVo.getSortNo());
                   Cell cell = row.getCell(cellIndex-1);
                   if (cell==null){
                       continue A;
                   }
                   String cellValue="";
                   boolean isMerge = isMergedRegion(sheet, i, cell.getColumnIndex());
                   if (isMerge){
                       cellValue  = getMergedRegionValue(sheet, cell.getRowIndex(), cell.getColumnIndex());
                   }else {
                       cellValue= getCellValue(cell);
                   }
                   String codeValue = pubDictDataVo.getCodeValue();
                   if (endFlag!=null&&StringUtils.isNotEmpty(codeValue)){
                       //结束列标识
                       if (endFlag.getCodeValue().equals(cellValue))
                           break A;
                   }
                   if (StringUtils.isEmpty(cellValue)&&"not_null".equals(codeValue)){
                       //数据列非空字段为空则认为导入结束
                       break A;
                   }
                   if (StringUtils.isEmpty(cellValue)&&"decimal".equals(pubDictDataVo.getListClass())){
                       cellValue="0";
                   }
                   if (StringUtils.isNotEmpty(cellValue)&& cellValue.contains("\\")){
                       cellValue=  cellValue.replaceAll("\\\\","-");
                   }
                   if (pubDictDataVo.getDictType().startsWith("TMPL-DATA") &&Constants.Y.equals(pubDictDataVo.getIsDefault())){
                       //特定值校验
                       String[] split = codeValue.split(",");
                       boolean check=false;
                       for (String value : split) {
                           if (value.equals(codeValue)){
                               check=true;
                           }
                       }
                       if (!check){
                           continue A;
                       }
                   }
                   if (Constants.Y.equals(pubDictDataVo.getIsDisplay())){
                       //需要保存数据库
                       sqlAdd.append("'").append(StringUtils.isEmpty(cellValue)?"":cellValue).append("'").append(",");
                   }
               }
                //默认值添加
                if (!CollectionUtils.isEmpty(infoEntity.getParaDictTypeList())){
                    for (PubDictDataVo pubDictDataVo : infoEntity.getParaDictTypeList()) {
                        if (Constants.Y.equals(pubDictDataVo.getIsDisplay())&&Constants.Y.equals(pubDictDataVo.getIsDefault())){
                            if (Constants.PRE_DAY.equals(pubDictDataVo.getCodeValue())){
                                Date date = DateUtils.addDays(new Date(), -1);
                                sqlAdd.append("'").append(DateUtils.dateTime(date)).append("'").append(",");
                            }else if (Constants.NOW_DAY.equals(pubDictDataVo.getCodeValue())){
                                sqlAdd.append("'").append(DateUtils.dateTime()).append("'").append(",");
                            }else   if (Constants.MERCHANT_ID.equals(pubDictDataVo.getCodeValue())){
                                sqlAdd.append("'").append(merchantId).append("'").append(",");
                            }else   if (Constants.IMPORT_BY.equals(pubDictDataVo.getCodeValue())){
                                sqlAdd.append("'").append(userName).append("'").append(",");
                            }else   if (Constants.APP_ID.equals(pubDictDataVo.getCodeValue())){
                                sqlAdd.append("'").append(appId).append("'").append(",");
                            } else {
                                sqlAdd.append("'").append(pubDictDataVo.getCodeValue()).append("'").append(",");

                            }
                        }
                    }
                }

                count++;
                sqlAdd.deleteCharAt(sqlAdd.length()-1).append("),");
                sqlSb.append(sqlAdd);
                if (count.compareTo(maxCount)>=0){
                    //TODO 数量超过最大数则保存数据库
                    sqlSb=  saveToDb(sql,sqlSb);
                    count=0;
                }
            }
            if (count>0){
                //TODO 剩余数据保存数据库
                saveToDb(sql,sqlSb);
            }

        } catch (CustomException e) {
            log.info("读取excel失败:{}",e.getMessage());
          throw e;
        }catch (Exception e) {
//            log.info("读取excel失败",e);
            e.printStackTrace();
            throw new CustomException("读取excel失败");
        }


        PubDictDataVo callDeal = getByCodeName(infoEntity.getFormatDictTypeList(), "callDeal");
        if (callDeal!=null&&"marketAttractiveRefresh".equals(callDeal.getCodeValue())){
            //更新
           try {
               for (int i = 1; i < 13; i++) {
                    String colId="prod_"+i;
                   baseMapper.annualBusinessPlanFlash(merchantId,DateUtils.dateTime(),entity.getTemplateNo(),appId,"市场吸引力",colId,"market_attractive_info_cache");
               }
           }catch (Exception e){
               log.error("市场吸引力关联刷新",e);
           }
        }
        if (callDeal!=null&&"companyCompetitivenessRefresh".equals(callDeal.getCodeValue())){
            //更新
            try {
                for (int i = 1; i < 13; i++) {
                    String colId="prod_"+i;
                    baseMapper.annualBusinessPlanFlash(merchantId,DateUtils.dateTime(),entity.getTemplateNo(),appId,"企业竞争力",colId, "company_competitiveness_cache");
                }
            }catch (Exception e){
                log.error("企业竞争力关联刷新",e);
            }
        }

        return JsonResults.success("导入成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonResults clearDataByTempNo(LoadExcelInfoEntity entity) {
        Long merchantId = SecurityUtils.getLoginUser().getMerchantId();
        LoadExcelInfoEntity infoEntity = this.selectByMultiId(entity);
        if (infoEntity==null){
            throw new CustomException("查询不到模板信息");
        }

        //TODO 查询字典数据
        setDictData(infoEntity, merchantId,entity.getTemplateNo());

        PubDictDataVo destTableName = getByCodeName(infoEntity.getFormatDictTypeList(), "destTableName");
        if (destTableName==null||StringUtils.isEmpty(destTableName.getCodeValue())){
            throw new CustomException("导入的目标表不能为空");
        }
        //TODO 清除指定日期数据
        if (StringUtils.isNotEmpty(entity.getClearStartDate())&&StringUtils.isNotEmpty(entity.getClearEndDate())){
            String clearSql="delete from "+destTableName.getCodeValue()+" where excel_tmpl_no='"+entity.getTemplateNo()+"' and import_date between '"+entity.getClearStartDate()
                    +"' and "+"'"+entity.getClearEndDate()+"'";
            try {
                jdbcTemplate.execute(clearSql);
                return JsonResults.success("执行成功");
            }catch (Exception e){
                log.error("清除指定日期数据失败:",e);
                throw new CustomException("清除指定日期数据失败");
            }

        }
        return JsonResults.error("执行失败");
    }

    public String getCellValue(Cell cell) {
        Object val = "";
        try {
            if (StringUtils.isNotNull(cell)) {
                CellType cellType = cell.getCellType();
                if (cellType == CellType.NUMERIC || cellType == CellType.FORMULA) {
                    val = cell.getNumericCellValue();
                    if (DateUtil.isCellDateFormatted(cell)) {
                        val = DateUtil.getJavaDate((Double) val); // POI Excel 日期格式转换
                        Date date=(Date)val;
                        val= DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,date);
                    } else {
                        val = new BigDecimal(val.toString()); // 浮点格式处理
                    }
                } else if (cellType== CellType.STRING) {
                    val = cell.getStringCellValue();
                } else if (cellType == CellType.BOOLEAN) {
                    val = cell.getBooleanCellValue();
                } else if (cellType== CellType.ERROR) {
                    val = cell.getErrorCellValue();
                }

            }
        } catch (Exception e) {
            return val.toString();
        }
        return val.toString();
    }
    /**
     * 获取合并单元格的值
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public String getMergedRegionValue(Sheet sheet ,int row , int column){
        int sheetMergeCount = sheet.getNumMergedRegions();
        for(int i = 0 ; i < sheetMergeCount ; i++){
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell) ;
                }
            }
        }

        return null ;
    }

    /**
     * 判断合并了行
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    private boolean isMergedRow(Sheet sheet,int row ,int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(row == firstRow && row == lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 判断指定的单元格是否是合并单元格
     * @param sheet
     * @param row 行下标
     * @param column 列下标
     * @return
     */
    private boolean isMergedRegion(Sheet sheet,int row ,int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断sheet页中是否含有合并单元格
     * @param sheet
     * @return
     */
    private boolean hasMerged(Sheet sheet) {
        return sheet.getNumMergedRegions() > 0 ? true : false;
    }

    private StringBuffer saveToDb(String sql, StringBuffer sqlAdd) {

        String execSql=sql+sqlAdd.toString();

        execSql = SqlTrim(execSql);

        try {
            jdbcTemplate.execute(execSql);
        }catch (Exception e){
            log.error("sql执行失败sql:{}",execSql,e);
            throw new CustomException("sql执行失败");
        }

      return  new StringBuffer();

    }

    private static String SqlTrim(String execSql) {

        if (execSql.endsWith(",")|| execSql.endsWith("(")){
            execSql = execSql.substring(0, execSql.length()-1);
            if (execSql.endsWith(",")|| execSql.endsWith("(")){
                return SqlTrim(execSql);
            }
        }
        return execSql;
    }

    /**
     * 表头校验
     * @param entity
     * @param sheet
     * @param isCheckHead
     */
    private static void checkHead(LoadExcelInfoEntity entity, Sheet sheet, PubDictDataVo isCheckHead) {
        if (isCheckHead !=null&& Constants.Y.equals(isCheckHead.getCodeValue())){
            //表头开始行号
            PubDictDataVo headStartRowNo = getByCodeName(entity.getFormatDictTypeList(), "headStartRowNo");
            //表头开始列号
            PubDictDataVo headStartColNo = getByCodeName(entity.getFormatDictTypeList(), "headStartColNo");
            //表头结束列号
            PubDictDataVo headEndColNo = getByCodeName(entity.getFormatDictTypeList(), "headEndColNo");

            //检查表头
            if (headStartRowNo !=null&& headStartColNo !=null&& headEndColNo !=null){
                //开始行号
                int headStartRow=0;
                int headStartCol=0;
                String headStartRowStr = headStartRowNo.getCodeValue();
                if (StringUtils.isNotEmpty(headStartRowStr)){
                    headStartRow = Integer.parseInt(headStartRowStr)-1;
                }
                String headStartColStr = headStartColNo.getCodeValue();
                if (StringUtils.isNotEmpty(headStartColStr)){
                    headStartCol = Integer.parseInt(headStartColStr)-1;
                }
                String headEndColStr = headEndColNo.getCodeValue();
                if (StringUtils.isEmpty(headEndColStr)){
                    throw new CustomException("表头结束列号值不能为空");
                }
                //结束列号
                int headEndCol = Integer.parseInt(headEndColStr)-1;

                Row row = sheet.getRow(headStartRow);
                for (int i = headStartCol; i < headEndCol; i++) {
                    Cell cell = row.getCell(i);
                    String stringCellValue = cell.getStringCellValue();
                    List<PubDictDataVo> headCheckList = entity.getDataDictTypeList();
                    PubDictDataVo pubDictDataVo = headCheckList.get(i);
                    if (!stringCellValue.equals(pubDictDataVo.getCodeLabel())){
                        log.error("表头校验不通过:{}:{}",stringCellValue,pubDictDataVo.getCodeLabel());
                        throw new CustomException("表头校验不通过");
                    }
                }


            }else{
                throw new CustomException("表头开始行号和开始列号和结束列号不能为空");
            }
        }
    }

    private void setDictData(LoadExcelInfoEntity entity, Long merchantId, @NotNull(message = "模板编号不能为空") String templateNo) {
        if (StringUtils.isNotEmpty(entity.getFormatDictType())){
            List<PubDictDataVo> dictDataByType = dictDataService.getDictDataByType(merchantId, entity.getFormatDictType(), null);
            if (!CollectionUtils.isEmpty(dictDataByType)){
                entity.setFormatDictTypeList(dictDataByType);
            }else {
                throw new CustomException("模板格式字典类型查询不到数据");
            }
        }else {
            throw new CustomException("模板格式字典类型不能为空");
        }
        if (StringUtils.isNotEmpty(entity.getDataDictType())){
            List<PubDictDataVo> dictDataByType = dictDataService.getDictDataByType(merchantId, entity.getDataDictType(), null);
            if (!CollectionUtils.isEmpty(dictDataByType)){
                entity.setDataDictTypeList(dictDataByType);
            }else {
                throw new CustomException("模板数据格式字典类型查询不到数据");
            }
        }else {
            throw new CustomException("模板数据格式字典类型不能为空");
        }
        if (StringUtils.isNotEmpty(entity.getParaDictType())){
            List<PubDictDataVo> dictDataByType = dictDataService.getDictDataByType(merchantId, entity.getParaDictType(), null);
            if (!CollectionUtils.isEmpty(dictDataByType)){
                entity.setParaDictTypeList(dictDataByType);
            }
        }
        entity.setParaDictTypeList(getDefaultVo(entity.getParaDictTypeList(),templateNo));

    }

   private  List<PubDictDataVo> getDefaultVo(List<PubDictDataVo> List,String templateNo){
        if (CollectionUtils.isEmpty(List)){
            List=new ArrayList<>();
        }
       PubDictDataVo import_date=new PubDictDataVo();
       import_date.setCodeName("import_date");
       import_date.setCodeValue(DateUtils.dateTime());
       import_date.setIsDefault("Y");
       import_date.setIsDisplay("Y");
       PubDictDataVo excel_tmpl_no=new PubDictDataVo();
       excel_tmpl_no.setCodeName("excel_tmpl_no");
       excel_tmpl_no.setCodeValue(templateNo);
       excel_tmpl_no.setIsDefault("Y");
       excel_tmpl_no.setIsDisplay("Y");
       List.add(import_date);
       List.add(excel_tmpl_no);
       return List;
    }
    private static PubDictDataVo getByCodeName(List<PubDictDataVo> list,String codeName){
        for (PubDictDataVo pubDictDataVo : list) {
            if (codeName.equals(pubDictDataVo.getCodeName()) ){
                return pubDictDataVo;
            }
        }
        return null;
    }
}
