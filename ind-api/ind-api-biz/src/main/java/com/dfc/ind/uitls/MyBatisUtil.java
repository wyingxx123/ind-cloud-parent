package com.dfc.ind.uitls;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dfc.ind.common.core.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;
import org.apache.ibatis.session.Configuration;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * mybatis工具类
 * @author zhouzhenhui
 */
@Slf4j
public class MyBatisUtil {

    private static Configuration configuration = new Configuration();



    /**
     * 将动态查询xml sql转换为可直接执行sql
     * @param selectXmlSql 如：select * from ... <if test=''></if>
     * @param parameterObject
     * @return
     * @throws Exception
     */
    public static String parseSelectXmlSqlToSql(String selectXmlSql, Object parameterObject) throws Exception {
//        String selectSql = "<select>" + replaceSpecialSymbols(selectXmlSql) + "</select>";
        //替换特殊字符
        selectXmlSql=selectXmlSql.replaceAll("<=","&lt;=").replaceAll("<","&lt;")
                .replaceAll(">=","&gt;=").replaceAll(">","&gt;");
        String selectSql = "<select>" + selectXmlSql+ "</select>";
        return parseXmlSqlToSql(selectSql, parameterObject);
    }

    /**
     * 将动态xml sql转换为可直接执行sql
     * @param xmlSql 如: <select>select * from ... <if test=''></if> </select>
     * @param parameterObject
     * @return
     * @throws Exception
     */
    public static String parseXmlSqlToSql(String xmlSql, Object parameterObject) throws Exception {
        Document doc = parseXmlToDocument(xmlSql);
        XNode xNode = new XNode(new XPathParser(doc, false), doc.getFirstChild(), null);
        XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, xNode);
        SqlSource sqlSource = xmlScriptBuilder.parseScriptNode();
        MappedStatement ms = new MappedStatement.Builder(configuration, UUID.randomUUID().toString(), sqlSource, null).build();
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        String executeSql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    executeSql = executeSql.replaceFirst("[?]", value instanceof String ? "'" + value + "'" : String.valueOf(value));
                }
            }
        }
        return executeSql;
    }

    /**
     * 将xmlSql转成Document
     * @param xmlSql
     * @return
     * @throws Exception
     */
    private static Document parseXmlToDocument(String xmlSql) throws Exception {
        if (StringUtils.isBlank(xmlSql)) {
            throw new CustomException("xmlSql不能为空");
        }
        try {
            DocumentBuilder builder = (DocumentBuilder)docBuildeIns.get();
            return builder.parse(new InputSource(new StringReader(xmlSql)));
        } catch (Exception e) {
            log.error("xmlSql解析异常：", e);
            throw new CustomException("xmlSql解析异常：", e);
        }
    }

    private static String replaceSpecialSymbols(String sql) {
        return sql.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    private static ThreadLocal docBuildeIns = new ThreadLocal() {
        protected DocumentBuilder initialValue() {
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                String msg = "DocumentBuilder 对象初始化失败！";
                log.error(msg, e);
                throw new IllegalStateException(msg, e);
            }
        }
    };

    public static String replaceSqlParam(String jobResource, String jobParam) throws Exception {
        if (!org.springframework.util.StringUtils.isEmpty(jobParam)) {
            if (jobParam.startsWith("[")) {
                List<Map> mapList = JSON.parseArray(jobParam, Map.class);
                StringBuilder sql = new StringBuilder();
                for (int i = 0; i < mapList.size(); i++) {
                    if (i == 0) {
                        sql.append(parseSelectXmlSqlToSql(jobResource, mapList.get(i)));
                    } else {
                        String str = parseSelectXmlSqlToSql(jobResource, mapList.get(i));
                        sql.append(",").append(str.split("\\) VALUES ")[1]);
                    }
                }
                jobResource = sql.toString();
            } else {

                Map map = JSON.parseObject(jobParam, Map.class);
                jobResource = parseSelectXmlSqlToSql(jobResource, map);
            }
        }
        return jobResource;
    }
}
 
 