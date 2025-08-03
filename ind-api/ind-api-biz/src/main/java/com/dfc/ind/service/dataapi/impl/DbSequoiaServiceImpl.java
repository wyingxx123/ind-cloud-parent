package com.dfc.ind.service.dataapi.impl;


import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.redis.service.RedisService;
import com.dfc.ind.entity.dataapi.param.ExecuteQuerySqlParam;
import com.dfc.ind.service.dataapi.IDbSequoiaService;
import com.dfc.ind.uitls.BasePageResult;
import com.dfc.ind.uitls.SequoiaPoolUtils;
import com.dfc.ind.uitls.SqlToJBsonUtils;
import com.sequoiadb.base.*;
import com.sequoiadb.base.options.InsertOption;
import com.sequoiadb.base.result.InsertResult;
import com.sequoiadb.base.result.UpdateResult;
import com.sequoiadb.datasource.SequoiadbDatasource;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BSONDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author huff
 * @since 2022-09-08
 */
@Service
@Slf4j
public class DbSequoiaServiceImpl implements IDbSequoiaService {

    @Autowired
    private RedisService redisService;

    @Override
    public int execUpdate(String sqlStr, ExecuteQuerySqlParam param) {
        Sequoiadb sequoiadb = null;
        DBCursor dbCursor = null;
        SequoiadbDatasource datasource = null;

        try {
            String collectionSpace = param.getSchemaName();
            if (StringUtils.isEmpty(collectionSpace)) {
                throw new CustomException("集合空间不能为空");
            }
            datasource = SequoiaPoolUtils.getConnection(param.getDataSourceId());
            if (datasource == null) {
                throw new CustomException("获取不到连接信息");
            }
            sequoiadb = datasource.getConnection();
            if (sequoiadb == null) {
                throw new CustomException("获取不到连接信息");
            }

            //从sql中解析出表名
            String updateTableName = getTableNameFromSql(sqlStr).toLowerCase();
            if (updateTableName.contains(".")){
                String[] split = updateTableName.split("\\.");
                collectionSpace=split[0];
                updateTableName=split[1];
            }
            CollectionSpace space = sequoiadb.getCollectionSpace(collectionSpace);
            DBCollection collection = space.getCollection(updateTableName);
            BasicBSONObject bsonObject = new BasicBSONObject();
            bsonObject.put("ReplSize",-1);
            collection.setAttributes(bsonObject);
            //从sql中解析出需要更新的字段和条件
            BasicBSONObject matcher = new BasicBSONObject();
            BasicBSONObject modifier = new BasicBSONObject();
            SqlToJBsonUtils.updateSqlToJBson(sqlStr, matcher, modifier);
            transferUpdateType(matcher, collection.getCSName(), collection.getName(), modifier);

            //执行更新
            UpdateResult updateResult = collection.upsertRecords(matcher, modifier);
            log.info("执行巨杉数据库upsertRecords:matcher:{},modifier:{}",matcher,modifier);

            return Integer.parseInt(String.valueOf(updateResult.getModifiedNum()));

        } catch (Exception e) {
            log.error("sql:{}执行失败{}",sqlStr, e);
            throw new CustomException("sql执行失败:"+e.getMessage());

        } finally {
            SequoiaPoolUtils.close(dbCursor, datasource, sequoiadb);
        }
    }

    private void transferUpdateType(BasicBSONObject matcher, String collectionSpace, String name, BasicBSONObject modifier) {
        String key = "filed.type:" + collectionSpace + ":" + name;
        for (String s : matcher.keySet()) {
            Object o = matcher.get(s);
            Object value = redisService.getCacheMapValue(key, s);
            if (value == null) {
                throw new CustomException("字段:" + s + "未缓存类型数据");
            }
            if (o instanceof BasicBSONObject) {
                BasicBSONObject bsonObject = (BasicBSONObject) o;

                String type = value.toString();
                switch (type) {
                    case "int":
                    case "smallint":
                        for (String s1 : bsonObject.keySet()) {
                            Object o1 = bsonObject.get(s1);
                            bsonObject.put(s1, Integer.parseInt(o1.toString()));
                            matcher.put(s, bsonObject);
                            break;
                        }
                        break;
                    case "bigint":
                        for (String s1 : bsonObject.keySet()) {
                            Object o1 = bsonObject.get(s1);
                            bsonObject.put(s1, Long.parseLong(o1.toString()));
                            matcher.put(s, bsonObject);
                            break;
                        }
                        break;
                    case "double":
                        for (String s1 : bsonObject.keySet()) {
                            Object o1 = bsonObject.get(s1);
                            bsonObject.put(s1, Double.parseDouble(o1.toString()));
                            matcher.put(s, bsonObject);
                            break;
                        }
                        break;
                    case "float":
                        for (String s1 : bsonObject.keySet()) {
                            Object o1 = bsonObject.get(s1);
                            bsonObject.put(s1, Float.parseFloat(o1.toString()));
                            matcher.put(s, bsonObject);
                            break;
                        }
                        break;
                    case "decimal":
                        for (String s1 : bsonObject.keySet()) {
                            Object o1 = bsonObject.get(s1);
                            bsonObject.put(s1, new BSONDecimal(o1.toString()));
                            matcher.put(s, bsonObject);
                            break;
                        }
                        break;

                    default:
                        break;
                }
            } else {
                String type = value.toString();
                try {
                    switch (type) {
                        case "int":
                        case "smallint":
                            matcher.put(s, Integer.parseInt(o.toString()));
                            break;
                        case "bigint":
                            matcher.put(s, Long.parseLong(o.toString()));
                            break;
                        case "double":
                            matcher.put(s, Double.parseDouble(o.toString()));
                            break;
                        case "float":
                            matcher.put(s, Float.parseFloat(o.toString()));
                            break;
                        case "decimal":
                            matcher.put(s, o.toString());
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    log.error("数据库字段类型转换失败", e);
                    throw new CustomException("数据库字段类型转换失败");
                }
            }
        }
        Object o = modifier.get("$set");
        BasicBSONObject bsonObject = (BasicBSONObject) o;
        for (String fieldName : bsonObject.keySet()) {
            Object value = redisService.getCacheMapValue(key, fieldName);
            if (value == null) {
                throw new CustomException("字段:" + fieldName + "未缓存类型数据");
            }
            String type = value.toString();
            switch (type) {
                case "int":
                case "smallint":
                    Object o1 = bsonObject.get(fieldName);
                    bsonObject.put(fieldName, Integer.parseInt(o1.toString()));
                    break;
                case "bigint":
                    bsonObject.put(fieldName, Long.parseLong(bsonObject.get(fieldName).toString()));
                    break;
                case "double":
                    bsonObject.put(fieldName, Double.parseDouble(bsonObject.get(fieldName).toString()));
                    break;
                case "float":
                    bsonObject.put(fieldName, Float.parseFloat(bsonObject.get(fieldName).toString()));

                    break;
                case "decimal":
                    bsonObject.put(fieldName, new BSONDecimal(bsonObject.get(fieldName).toString()));
                    break;
                default:
            }
        }

        modifier.put("$set", bsonObject);
    }


    public int execInsert(String sqlStr, ExecuteQuerySqlParam param) {
        Sequoiadb sequoiadb = null;
        DBCursor dbCursor = null;
        SequoiadbDatasource datasource = null;
        try {
            String collectionSpace = param.getSchemaName();
            if (StringUtils.isEmpty(collectionSpace)) {
                throw new CustomException("集合空间不能为空");
            }

            datasource = SequoiaPoolUtils.getConnection(param.getDataSourceId());
            if (datasource == null) {
                throw new CustomException("获取不到连接信息");
            }
            sequoiadb = datasource.getConnection();
            if (sequoiadb == null) {
                throw new CustomException("获取不到连接信息");
            }
            //sql中截取出表名
            String collectionName = getTableNameFromSql(sqlStr).toLowerCase();
            //连接指定要操作的空间(数据库schema)
            if (collectionName.contains(".")){
                String[] split = collectionName.split("\\.");
                collectionSpace=split[0];
                collectionName=split[1];
            }
            CollectionSpace space = sequoiadb.getCollectionSpace(collectionSpace);
            //空间指定要操作的集合(表)
            DBCollection collection = space.getCollection(collectionName);
            //从sql中截取要新增的对象
            List<BSONObject> objectList = SqlToJBsonUtils.InsertOrReplaceSqlToJBson(sqlStr);
            //操作方式主键存在则覆盖
            InsertOption insertOption = new InsertOption();
            insertOption.setFlag(InsertOption.FLG_INSERT_REPLACEONDUP);
            //执行新增或修改
            InsertResult insertResult = collection.bulkInsert(objectList, insertOption);
            log.info("执行巨杉数据库bulkInsert:objectList:{},insertOption:{}",objectList,insertOption);

            //计算新增和修改的行数
            long updNum = insertResult.getInsertNum() + insertResult.getModifiedNum();

            return Integer.parseInt(String.valueOf(updNum));
        } catch (Exception e) {
            log.error("sql执行失败", e);
            throw new CustomException("sql执行失败");

        } finally {
            SequoiaPoolUtils.close(dbCursor, datasource, sequoiadb);
        }

    }

    public BasePageResult<LinkedHashMap<String, Object>> executeQuery(ExecuteQuerySqlParam param) throws Exception {
        Sequoiadb sequoiadb = null;
        DBCursor dbCursor = null;
        SequoiadbDatasource datasource = null;
        String sql = param.getSql();
        long totalCount = 0;
        List<LinkedHashMap<String, Object>> resultList = new ArrayList<>();
        String collectionSpace = param.getSchemaName();
        if (StringUtils.isEmpty(collectionSpace)) {
            throw new CustomException("集合空间不能为空");
        }
        String collectionName = getTableNameFromSql(sql).toLowerCase();

        try {
            datasource = SequoiaPoolUtils.getConnection(param.getDataSourceId());
            if (datasource == null) {
                throw new CustomException("获取不到连接源信息");
            }
            sequoiadb = datasource.getConnection();
            if (sequoiadb == null) {
                throw new CustomException("获取不到连接信息");
            }
            if (collectionName.contains(".")){
                String[] split = collectionName.split("\\.");
                collectionSpace=split[0];
                collectionName=split[1];
            }
            CollectionSpace space = sequoiadb.getCollectionSpace(collectionSpace);
            DBCollection collection = space.getCollection(collectionName);
            // 封装bson对象
            DBQuery dbQuery = new DBQuery();
            BasicBSONObject matcher = new BasicBSONObject();
            BasicBSONObject selector = new BasicBSONObject();
            BasicBSONObject orderBy = new BasicBSONObject();
            LinkedHashMap<String, String> returnFiled = new LinkedHashMap<>(50);
            //sql转bson
            SqlToJBsonUtils.querySqlToJBson(sql, dbQuery, matcher, selector, orderBy, returnFiled);
            long skipRows = (long) (param.getPageIndex() - 1) * param.getPageSize();
            long returnRows = param.getPageSize();
            //字段类型转换
            transferQueryType(matcher, collection.getCSName(), collection.getName());
            //分页总数查询
            totalCount = collection.getCount(matcher);
            dbCursor = collection.query(matcher, selector, orderBy, null, skipRows, returnRows);
            log.info("执行巨杉数据库query:matcher:{},selector:{},orderBy:{},skipRows:{},returnRows:{}",matcher,selector,orderBy,skipRows,returnRows);
            while (dbCursor.hasNext()) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>(16);
                BSONObject next = dbCursor.getNext();
                for (String s : returnFiled.keySet()) {
                    map.put(returnFiled.get(s), next.get(s));
                }
                resultList.add(map);
            }
        } catch (Exception e) {
            log.error("异常sql：{}", sql, e);
            throw new CustomException(e.getMessage());
        } finally {
            SequoiaPoolUtils.close(dbCursor, datasource, sequoiadb);
        }
        return new BasePageResult<>(resultList, totalCount);
    }


    private void transferQueryType(BasicBSONObject matcher, String collectionSpace, String name) {
        String key = "filed.type:" + collectionSpace + ":" + name;
        for (String s : matcher.keySet()) {
            Object o = matcher.get(s);
            Object value = redisService.getCacheMapValue(key, s);
            if (value == null) {
                throw new CustomException("字段:" + s + "未缓存类型数据");
            }
            if (o instanceof BasicBSONObject) {
                BasicBSONObject bsonObject = (BasicBSONObject) o;

                String type = value.toString();
                switch (type) {
                    case "int":
                    case "smallint":
                        for (String s1 : bsonObject.keySet()) {
                            Object o1 = bsonObject.get(s1);
                            bsonObject.put(s1, Integer.parseInt(o1.toString()));
                            matcher.put(s, bsonObject);
                            break;
                        }
                        break;
                    case "bigint":
                        for (String s1 : bsonObject.keySet()) {
                            Object o1 = bsonObject.get(s1);
                            bsonObject.put(s1, Long.parseLong(o1.toString()));
                            matcher.put(s, bsonObject);
                            break;
                        }
                        break;
                    case "double":
                        for (String s1 : bsonObject.keySet()) {
                            Object o1 = bsonObject.get(s1);
                            bsonObject.put(s1, Double.parseDouble(o1.toString()));
                            matcher.put(s, bsonObject);
                            break;
                        }
                        break;
                    case "float":
                        for (String s1 : bsonObject.keySet()) {
                            Object o1 = bsonObject.get(s1);
                            bsonObject.put(s1, Float.parseFloat(o1.toString()));
                            matcher.put(s, bsonObject);
                            break;
                        }
                        break;
                    case "decimal":
                        for (String s1 : bsonObject.keySet()) {
                            Object o1 = bsonObject.get(s1);
                            bsonObject.put(s1, new BSONDecimal(o1.toString()));
                            matcher.put(s, bsonObject);
                            break;
                        }
                        break;

                    default:
                        break;
                }
            } else {
                String type = value.toString();
                try {
                    switch (type) {
                        case "int":
                        case "smallint":
                            matcher.put(s, Integer.parseInt(o.toString()));
                            break;
                        case "bigint":
                            matcher.put(s, Long.parseLong(o.toString()));
                            break;
                        case "double":
                            matcher.put(s, Double.parseDouble(o.toString()));
                            break;
                        case "float":
                            matcher.put(s, Float.parseFloat(o.toString()));
                            break;
                        case "decimal":
                            matcher.put(s, o.toString());
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    log.error("数据库字段类型转换失败", e);
                    throw new CustomException("数据库字段类型转换失败");
                }
            }
        }
    }


    private String getTableNameFromSql(String sql) {

        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            throw new CustomException("sql解析失败");
        }
        // 获取table
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableList = tablesNamesFinder.getTableList(statement);
        if (CollectionUtils.isEmpty(tableList)){
            throw new CustomException("截取表名失败");
        }else {
            return tableList.get(0);
        }

    }
}
