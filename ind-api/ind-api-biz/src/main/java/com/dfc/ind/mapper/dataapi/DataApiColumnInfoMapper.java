package com.dfc.ind.mapper.dataapi;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfc.ind.entity.dataapi.DataApiColumnInfoEntity;
import com.dfc.ind.entity.dataapi.vo.ApiColumnVo;
import com.dfc.ind.entity.dataapi.vo.ApiSqlInfoVo;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * api字段配置表 Mapper 接口
 * </p>
 *
 * @author huff
 * @since 2024-04-07
 */
public interface DataApiColumnInfoMapper extends MppBaseMapper<DataApiColumnInfoEntity> {

    List<ApiSqlInfoVo> getApiEngineInfo(String appId);

    List<ApiColumnVo> getColumnData(@Param("set") Set<String> tableSet);

    void updateApiColumnStatus(@Param("list") List<String> serviceIdList,@Param("appId") String appId);

    void saveColumnInfo(List<DataApiColumnInfoEntity> dataApiColumnInfoEntities);

    IPage<DataApiColumnInfoEntity> pageList(Page startPage,@Param("entity") DataApiColumnInfoEntity entity);

}
