package com.dfc.ind.uitls;

import com.dfc.ind.common.core.exception.CustomException;
import com.sequoiadb.base.DBCursor;
import com.sequoiadb.base.Sequoiadb;
import com.sequoiadb.datasource.SequoiadbDatasource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Cyan_RA9
 * @version : 21.0
 */
@Slf4j
@Component
public class SequoiaPoolUtils {

    private static ConcurrentHashMap<String, SequoiadbDatasource> dataSourceMap;

    public static ConcurrentHashMap<String, SequoiadbDatasource> getDataSourceMap() {
        return dataSourceMap;
    }



    static {
        try {
            if (dataSourceMap==null){
                dataSourceMap=new ConcurrentHashMap<>();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static SequoiadbDatasource getConnection(String id) throws SQLException {
        if (dataSourceMap.containsKey(id)){
           /* SequoiadbDatasource dataSource =dataSourceMap.get(id);
            long activeCount = dataSource.getUsedConnNum();
            int poolingCount= dataSource.getIdleConnNum();
            int maxActive = dataSource.getDatasourceOptions().getMaxCount();
            System.out.println("sequoiaDb连接池的总容量maxActive = " + maxActive);
            System.out.println("sequoiaDb空闲的连接数poolingCount = " + poolingCount);
            System.out.println("sequoiaDb已使用的连接数activeCount = " + activeCount);*/
            try {
                return   dataSourceMap.get(id);
            } catch (Exception e) {
                throw new CustomException("获取sequoiaDb连接失败");
            }
        }else {
           return  null;
        }


    }


    /**
     * 将连接归还连接池
     * @param dbCursor
     * @param sequoiadbDatasource
     * @param sequoiadb
     */
    public static void close(DBCursor dbCursor, SequoiadbDatasource sequoiadbDatasource, Sequoiadb sequoiadb) {
        try {
            if (dbCursor != null) {
                dbCursor.close();
            }
            if (sequoiadbDatasource != null&&sequoiadb != null) {
                sequoiadbDatasource.releaseConnection(sequoiadb);
            }

        } catch (Exception e) {
           log.error("释放sdb连接失败",e);
            throw new CustomException("释放sdb连接失败");
        }
    }
}