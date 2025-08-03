package com.dfc.ind.uitls;

import com.alibaba.druid.pool.DruidDataSource;
import com.dfc.ind.common.core.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Cyan_RA9
 * @version : 21.0
 */
@Slf4j
@Component
public class JDBCUtilsDruid {

    private static final ConcurrentHashMap<String,DataSource> dataSourceMap  =new ConcurrentHashMap<>();;

    public static ConcurrentHashMap<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }

    public static Connection getConnection(String id) throws SQLException {
        if (dataSourceMap.containsKey(id)){
            DruidDataSource dataSource =(DruidDataSource) dataSourceMap.get(id);
            long activeCount = dataSource.getActiveCount();
            int poolingCount = dataSource.getPoolingCount();
            int maxActive = dataSource.getMaxActive();
            log.info("连接池的总容量maxActive = " + maxActive);
            log.info("空闲的连接数poolingCount = " + poolingCount);
            log.info("已使用的连接数activeCount = " + activeCount);
            return   dataSourceMap.get(id).getConnection();
        }else {
            return  null;
        }
    }

    //释放资源
    /**
     * 1.使用了数据库连接池技术后，close并不是真正地关闭与数据库的连接，
     * 而只是取消了对连接池中连接的引用，将所用完的Connection对象放回了连接池。
     * 2.简单地说，由于Connection本身是个接口，因此根据动态绑定机制，实际中调用
     * 的close方法可以来自不同的实现类，底层处理机制也自然不尽相同。
     */
    public static void closeDataSource(DruidDataSource dataSource) {
        try {
            if (dataSource != null) {
                dataSource.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("失败");
        }
    }

    public static void close(ResultSet resultSet, Statement statement, Connection connection) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CustomException("连接信息为空");
        }
    }
}