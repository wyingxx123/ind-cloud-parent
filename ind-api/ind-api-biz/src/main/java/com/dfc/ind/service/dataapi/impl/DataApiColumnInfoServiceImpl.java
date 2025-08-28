package com.dfc.ind.service.dataapi.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.utils.SecurityUtils;
import com.dfc.ind.entity.dataapi.DataApiColumnInfoEntity;
import com.dfc.ind.entity.dataapi.DataApiInfoEntity;
import com.dfc.ind.entity.dataapi.vo.ApiColumnVo;
import com.dfc.ind.entity.dataapi.vo.ApiSqlInfoVo;
import com.dfc.ind.mapper.dataapi.DataApiColumnInfoMapper;
import com.dfc.ind.service.dataapi.IDataApiColumnInfoService;
import com.dfc.ind.service.dataapi.IDataApiInfoService;
import com.dfc.ind.uitls.MyBatisUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * api字段配置表 服务实现类
 * </p>
 *
 * @author huff
 * @since 2024-04-07
 */
@Service
public class DataApiColumnInfoServiceImpl extends ServiceImpl<DataApiColumnInfoMapper, DataApiColumnInfoEntity> implements IDataApiColumnInfoService {



    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonResults syncColumnInfo(String appId) {

        List<DataApiColumnInfoEntity> dataApiColumnInfoEntities=new ArrayList<>();
        //查询未持久化字段的查询类型api
        List<ApiSqlInfoVo> apiSqlInfoVoList= baseMapper.getApiEngineInfo(appId);
        Set<String> tableSet=new HashSet<>();
        Map<String,String> fieldMap=new HashMap<>();
        if (CollectionUtils.isEmpty(apiSqlInfoVoList)){
            return JsonResults.success("查询不到未持久化字段的api数据");
        }
        //解析api中的sql提取表名和字段名,as别名
        for (ApiSqlInfoVo apiSqlInfoVo : apiSqlInfoVoList) {
            Statement statement = null;
            String engineResource = apiSqlInfoVo.getEngineResource();
            HashMap<Object, Object> map = new HashMap<>();
            try {
               String sqlStr = MyBatisUtil.parseSelectXmlSqlToSql(engineResource, map);
                statement = CCJSqlParserUtil.parse(sqlStr);
                if (statement instanceof Select){
                    TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
                    List<String> tableList = tablesNamesFinder.getTableList(statement);
                    HashSet<String> strings = new HashSet<>(tableList);
                    tableSet.addAll(strings);
                    Select selectStatement = (Select) statement;
                    PlainSelect plain = (PlainSelect) selectStatement.getSelectBody();
                    for (SelectItem selectItem : plain.getSelectItems()) {
                        SimpleNode astNode = selectItem.getASTNode();
                        String firstToken = astNode.jjtGetFirstToken().toString();
                        String lastToken = astNode.jjtGetLastToken().toString();
                        fieldMap.put(firstToken.replaceAll("`",""), lastToken);
                    }
                }
            } catch (Exception e) {
               log.error("同步column数据:sql解析错误",e);
            }

        }

        if (!CollectionUtils.isEmpty(tableSet)){
            //根据表名查询所有的字段
            List<ApiColumnVo> columnData = baseMapper.getColumnData(tableSet);
            if (!CollectionUtils.isEmpty(columnData)){
                for (ApiColumnVo columnVo : columnData) {
                    String columnName = columnVo.getColumnName();
                    String columnDesc = columnVo.getColumnDesc();
                    if (fieldMap.containsKey(columnName)){
                        DataApiColumnInfoEntity entity=new DataApiColumnInfoEntity();
                        String value = fieldMap.get(columnName);
                        if (StringUtils.isNotEmpty(value)){
                            entity.setColumnNo(value);

                        }else {
                            entity.setColumnNo(columnName);
                        }
                        entity.setColumnDesc(columnDesc);
                        entity.setAppId(appId);
                        entity.setCreateBy(SecurityUtils.getUserName());
                        entity.setCreateTime(new Date());
                        dataApiColumnInfoEntities.add(entity);
                    }
                }
            }
            //保存字段信息
            baseMapper.saveColumnInfo(dataApiColumnInfoEntities);
            //更新api字段持久化状态
            List<String> serviceIdList = apiSqlInfoVoList.stream().map(ApiSqlInfoVo::getServiceId).collect(Collectors.toList());
            baseMapper.updateApiColumnStatus(serviceIdList,appId);
        }

        return JsonResults.success("同步成功");
    }

    @Override
    public JsonResults pageList(Page startPage, DataApiColumnInfoEntity entity) {



        return JsonResults.success( baseMapper.pageList(startPage,entity));
    }
}
