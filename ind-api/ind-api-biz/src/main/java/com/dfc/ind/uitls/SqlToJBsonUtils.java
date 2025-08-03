package com.dfc.ind.uitls;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dfc.ind.common.core.exception.CustomException;
import com.sequoiadb.base.DBQuery;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.*;

/**
 * sql转JBson工具类
 */
@Slf4j
public class SqlToJBsonUtils {

    /**
     * sql转bson
     *
     * @param sql
     * @param dbQuery
     * @param matcher
     * @param selector
     * @param orderBy
     * @param fieldMap
     */
    public static void querySqlToJBson(String sql, DBQuery dbQuery, BasicBSONObject matcher, BasicBSONObject selector, BasicBSONObject orderBy, LinkedHashMap<String, String> fieldMap) {
        Statement statement=null;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        }catch (Exception e){
            log.error("sql解析失败sql:{}",sql,e);
            throw new CustomException("sql解析失败");
        }
        // 获取 join中的table
        Select selectStatement = (Select) statement;
        PlainSelect plain = (PlainSelect) selectStatement.getSelectBody();
        for (SelectItem selectItem : plain.getSelectItems()) {
            SimpleNode astNode = selectItem.getASTNode();
            String firstToken = astNode.jjtGetFirstToken().toString();
            String lastToken = astNode.jjtGetLastToken().toString();
            fieldMap.put(firstToken.replaceAll("`",""), lastToken);
        }
        for (String key : fieldMap.keySet()) {
            selector.put(key, "");
        }
        dbQuery.setSelector(selector);
        // 获取 where条件
        Expression where_expression = plain.getWhere();
        String whereStr = where_expression.toString();

        if (whereStr != null && !"".equals(whereStr)) {
            setMatcher(whereStr, matcher);
        }

        // 获取order by
        List<OrderByElement> OrderByElements = plain.getOrderByElements();
        if (OrderByElements != null) {
            for (OrderByElement orderByElement : OrderByElements) {
                Column column = (Column) orderByElement.getExpression();
                boolean asc = orderByElement.isAsc();
                if (asc) {
                    orderBy.put(column.getColumnName(), 1);
                } else {
                    orderBy.put(column.getColumnName(), -1);
                }
            }
        }


    }

    private static void setMatcher(String andString, BasicBSONObject matcher) {
        String[] andSplit = andString.split(" AND ");
        for (String andStr : andSplit) {
            if (andStr.contains("1 = 1")||andStr.contains("1=1")) {
                continue;
            }
            String s = andStr.trim().replaceAll("'", "").replaceAll("\"", "");
            if (s.contains(">=")) {
                String[] and = s.split(">=");
                BasicBSONObject queryBson = new BasicBSONObject();
                queryBson.put("$gte", and[1].trim());
                matcher.put(and[0].trim(), queryBson);
            } else if (s.contains("<=")) {
                String[] and = s.split("<=");
                BasicBSONObject queryBson = new BasicBSONObject();
                queryBson.put("$lte", and[1].trim());
                matcher.put(and[0].trim(), queryBson);
            }else if (s.contains("<>")) {
                String[] and = s.split("<>");
                BasicBSONObject queryBson = new BasicBSONObject();
                queryBson.put("$ne", and[1].trim());
                matcher.put(and[0].trim(), queryBson);
            }  else if (s.contains(">")) {
                String[] and = s.split(">");
                BasicBSONObject queryBson = new BasicBSONObject();
                queryBson.put("$gt", and[1].trim());
                matcher.put(and[0].trim(), queryBson);
            } else if (s.contains("<")) {
                String[] and = s.split("<");
                BasicBSONObject queryBson = new BasicBSONObject();
                queryBson.put("$lt", and[1].trim());
                matcher.put(and[0].trim(), queryBson);
            } else if (s.contains("!=")) {
                String[] and = s.split("!=");
                BasicBSONObject queryBson = new BasicBSONObject();
                queryBson.put("$ne", and[1].trim());
                matcher.put(and[0].trim(), queryBson);
            } else if (s.contains("=")) {
                String[] and = s.split("=");
                matcher.put(and[0].trim(), and[1].trim());
            } else if (s.contains("LIKE")) {
                //db.sample.employee.find( { str: { $regex: 'dh.*fj', $options: 'i' } } );
                if (s.contains("CONCAT")) {
                    String[] concatSplit = s.split("CONCAT");
                    String sub = concatSplit[1].trim().substring(1, concatSplit[1].length() - 1);
                    String[] likeArr = sub.split(",");
                    if (likeArr.length == 3) {
                        //全模糊
                        BasicBSONObject queryBson = new BasicBSONObject();
                        queryBson.put("$regex", likeArr[1].trim());
                        queryBson.put("$options", "i");
                        matcher.put(concatSplit[0].trim(), queryBson);
                    } else if (likeArr[0].contains("%")) {
                        //左模糊
                        BasicBSONObject queryBson = new BasicBSONObject();
                        queryBson.put("$regex", ".*" + likeArr[1].trim());
                        queryBson.put("$options", "m");
                        matcher.put(concatSplit[0].trim(), queryBson);

                    } else {
                        //右模糊
                        BasicBSONObject queryBson = new BasicBSONObject();
                        queryBson.put("$regex", "^" + likeArr[1].trim() + "*");
                        queryBson.put("$options", "m");
                        matcher.put(concatSplit[0].trim(), queryBson);
                    }
                } else {
                    String[] likeSplit = s.split("LIKE");

                    String s1 = likeSplit[1].trim();
                    if (s1.startsWith("%") && s1.endsWith("%")) {
                        //全模糊
                        BasicBSONObject queryBson = new BasicBSONObject();
                        queryBson.put("$regex", s1.substring(1, s1.length() - 1));
                        matcher.put(likeSplit[0].trim(), queryBson);
                    } else if (s1.startsWith("%")) {
                        //左模糊
                        BasicBSONObject queryBson = new BasicBSONObject();
                        queryBson.put("$regex", ".*" + s1.substring(1, s1.length() - 1));
                        matcher.put(likeSplit[0].trim(), queryBson);

                    } else {
                        //右模糊
                        BasicBSONObject queryBson = new BasicBSONObject();
                        queryBson.put("$regex", "^" + s1.substring(0, s1.length() - 1) + "*");
                        matcher.put(likeSplit[0].trim(), queryBson);

                    }
                }

            } else if (s.contains(" IS NULL")) {
                String[] and = s.split(" IS NULL");
                BasicBSONObject queryBson = new BasicBSONObject();
                queryBson.put("$isnull", 1);
                matcher.put(and[0].trim(), queryBson);

            } else if (s.contains(" NOT IN")) {
                String[] and = s.split(" NOT IN");
                String value = and[1].trim();
                BasicBSONObject queryBson = new BasicBSONObject();
                queryBson.put("$nin", Arrays.asList(value.substring(1, value.length() - 1).split(",")));
                matcher.put(and[0].trim(), queryBson);
            } else if (s.contains(" IN")) {
                String[] and = s.split(" IN");
                BasicBSONObject queryBson = new BasicBSONObject();
                String value = and[1].trim();
                queryBson.put("$in", Arrays.asList(value.substring(1,value.length() - 1).split(",")));
                matcher.put(and[0].trim(), queryBson);
            }
        }
    }

    /**
     * 只支持单表
     *
     * @param sql sql
     * @return BasicBSONObject
     */
    public static List<BSONObject> InsertOrReplaceSqlToJBson(String sql) {
        List<BSONObject> list = new ArrayList<>();
        String[] split = sql.split("\\) VALUES ");
        String[] split1 = split[0].split("\\(");
        String s = split1[1];
        String[] filedList = s.split(",");
        String valueString = split[1].trim().substring(1, split[1].length() - 1);
        String[] valueArr = valueString.split("\\),");
        for (String valueStr : valueArr) {
            if (valueStr.startsWith("(")) {
                valueStr = valueStr.substring(1);
            }
            JSONArray objects = JSON.parseArray("[" + valueStr + "]");
            BSONObject bsonObject = new BasicBSONObject();
            for (int i = 0; i < objects.size(); i++) {
                Object o = objects.get(i);
                bsonObject.put(filedList[i], o);
            }
            list.add(bsonObject);
        }
        return list;
    }

    /**
     * 只支持单表
     * update table set id=xx,name=xx where id=21 and name =21
     */
    public static void updateSqlToJBson(String sql, BasicBSONObject matcher, BasicBSONObject modifier) {
        if (sql.contains(" WHERE ")) {
            String[] split = sql.split(" WHERE ");
            //例如:id=21 and name =21
            String whereSql = split[1];
            setMatcher(whereSql, matcher);
            //例如:id=xx , name=xx
            String setSql = split[0].split(" SET ")[1];
            BasicBSONObject update = new BasicBSONObject();
            if (setSql.contains(" AND ")) {
                String[] setSplit = setSql.split(" AND ");
                for (String setStr : setSplit) {
                    String[] setValue = setStr.split("=");
                    String value = setValue[1];
                    update.put(setValue[0].trim(), value.trim().replaceAll("'", "").replaceAll("\"", ""));
                }
            } else {
                String[] setSplit = setSql.split(",");
                for (String setStr : setSplit) {
                    String[] setValue = setStr.split("=");
                    String value = setValue[1];
                    update.put(setValue[0].trim(), value.trim().replaceAll("'", "").replaceAll("\"", ""));
                }
            }


            modifier.put("$set", update);
        } else {
            throw new CustomException("不允许不带条件的更新");
        }
    }

    /**
     * 判断是否走mysql执行
     *
     * @param sqlStr
     * @return
     */
    public static Boolean isToMysql(String sqlStr) {
        Statement statement = null;
        try {
            if (sqlStr.contains("CASE WHEN")){
                return true;
            }
            if (sqlStr.split(" FROM ").length>2){
                return true;
            }
            if (sqlStr.contains(" GROUP BY ")){
                return true;
            }
            if (sqlStr.contains(" GROUP CONCAT")){
                return true;
            }
            statement = CCJSqlParserUtil.parse(sqlStr);
        } catch (JSQLParserException e) {
            log.error("sqlParser解析失败sql:{}", sqlStr, e);
            throw new CustomException("sqlParser解析失败sql:" + sqlStr);
        }
        // 获取table
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableList = tablesNamesFinder.getTableList(statement);
        LinkedHashSet<String> linked = new LinkedHashSet<>(tableList);
        if (linked.size()>1){
            return true;
        }
        if (statement instanceof Select){
            Select select= (Select)statement;
            PlainSelect selectBody = (PlainSelect)select.getSelectBody();
            GroupByElement groupBy = selectBody.getGroupBy();
            return groupBy != null;
        }else if (statement instanceof Update){
            Update update= (Update)statement;
            Select select = update.getSelect();
            if (select!=null){
                return true;
            }
            return false;
        }else if (statement instanceof Replace){
            Replace replace= (Replace)statement;
            ItemsList itemsList = replace.getItemsList();
            if (itemsList instanceof SubSelect){
                return true;
            }
            return false;
        }else if (statement instanceof Insert){
            Insert insert= (Insert)statement;
            Select select = insert.getSelect();
            if (select!=null){
                return true;
            }
            return false;
        }else if (statement instanceof Delete){
            log.error("不支持的sql类型:sql{}",sqlStr);
            throw new CustomException("不支持的sql类型");
        }else {
            log.error("不支持的sql类型:sql{}",sqlStr);
            throw new CustomException("不支持的sql类型");
        }

    }

    public static String sqlToUpperCase(String sqlStr) {
        sqlStr = sqlStr.trim() .replaceAll("\r\n|\\s+", " ").replaceAll("update ", "UPDATE ")
                .replaceAll("select ", "SELECT ")
                .replaceAll("insert into ", "INSERT INTO ")
                .replaceAll("replace into ", "REPLACE INTO ")
                .replaceAll(" and ", " AND ")
                .replaceAll(" as ", " AS ")
                .replaceAll(" in ", " IN ")
                .replaceAll(" not in ", " NOT IN ")
                .replaceAll("case when", "CASE WHEN")
                .replaceAll(" like ", " LIKE ")
                .replaceAll(" where ", " WHERE ")
                .replaceAll(" concat", " CONCAT")
                .replaceAll(" group concat", " GROUP CONCAT")
                .replaceAll(" from ", " FROM ")
                .replaceAll("left join", "LEFT JOIN")
                .replaceAll("right join", "RIGHT JOIN")
                .replaceAll(" from ", " FROM ")
                .replaceAll(" is not null", " IS NOT NULL")
                .replaceAll(" is null", " IS NULL")
        ;
        return sqlStr;
    }

    public static boolean isInsert(String sqlStr) {
        return sqlStr.startsWith("INSERT INTO") || sqlStr.startsWith("REPLACE INTO");
    }
}
