package com.dfc.ind.feign;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysDictData;
import com.dfc.ind.feign.fallback.SysDiceDataServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 描述: 字典信息feign接口
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2021/3/9
 * @copyright 武汉数慧享智能科技有限公司
 */
@FeignClient(value = "ind-system", fallback = SysDiceDataServiceFallback.class)
public interface ISysDictDataService {

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/dict/data/type/{dictType}")
    JsonResults dictType(@PathVariable String dictType);

    /**
     * 查询字典信息
     * @param entity
     * @return
     */
    @PostMapping(value = "/dict/data/listAll")
    JsonResults listAll(@RequestBody SysDictData entity);

    /**
     * 新增字典
     * @param entity
     * @return
     */
    @PostMapping(value = "/dict/data")
    JsonResults add(@RequestBody SysDictData entity);
}
