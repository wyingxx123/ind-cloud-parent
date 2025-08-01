package com.dfc.ind.entity.dataapi.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 描述:
 * </p>
 *
 * @author huff
 * @date 2022/9/13 9:55
 */
@Data
public class RequestParamVo implements Serializable {
    private List<String> serviceIdList;
    private String createBy;
    private String company;
}
