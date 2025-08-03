package com.dfc.ind.service;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.LoadExcelInfoEntity;
import com.github.jeffreyning.mybatisplus.service.IMppService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 导入模板信息 服务类
 * </p>
 *
 * @author huff
 * @since 2024-07-22
 */
public interface ILoadExcelInfoService extends IMppService<LoadExcelInfoEntity> {

    JsonResults importData(MultipartFile file, LoadExcelInfoEntity entity);

    JsonResults clearDataByTempNo(LoadExcelInfoEntity entity);
}
