package com.dfc.ind.feign.fallback;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysDictData;
import com.dfc.ind.feign.ISysDictDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 描述: 字典信息fallback类
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2021/3/9
 * @copyright 武汉数慧享智能科技有限公司
 */
@Slf4j
@Service
public class SysDiceDataServiceFallback implements ISysDictDataService {

    @Override
    public JsonResults dictType(String dictType) {
        log.error("查询字典信息失败");
        return JsonResults.error("查询字典信息失败");
    }

    @Override
    public JsonResults listAll(SysDictData entity) {
        log.error("查询字典信息失败 >> {}",entity);
        return JsonResults.error("查询字典信息失败");
    }

    @Override
    public JsonResults add(SysDictData entity) {
        return JsonResults.error("新增字典失败");
    }
}
