package com.dfc.ind.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 描述:产品计划统计表
 * </p>
 *
 * @author huff
 * @date 2023/5/17
 */

@Data
public class PubDictDataVo implements Serializable {

    /**
     * 字典类型
     */
    private  String dictType;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典编号
     */
    private String dictCode;

    /**
     * 排序
     */
    private String sortNo;


    private  String codeName;

    private  String codeLabel;


    private  String  codeValue ;

    private String listClass;
    private  String isDefault;
    private  String isDisplay;
    private  String description;
    private  String resource;

    private  Long merchantId;
}
