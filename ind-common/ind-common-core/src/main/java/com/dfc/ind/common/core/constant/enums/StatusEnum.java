package com.dfc.ind.common.core.constant.enums;

public enum StatusEnum {
//	status_finish("完成", "00"),
//	status_atelic("未完成", "01"),
//	status_defer("延期", "03"),
//	status_abate("失效","02"),
    status_user_delete("销户","1"),
    status_user_normal("正常","0"),
    status_user_waitApproval("待审批为商户","2"),
    status_user_merchant("商户身份","3"),
    status_user_refuse("拒绝成为商户身份状态","4"),
    status_in_process("处理中","05"),
    status_others("其它","09"),
    status_cancel("取消","06"),
    status_notApprove("未审批","00"),
    status_approve("已审批","01"),
    status_notDefault("非默认","0"),
    status_default("默认","1"),
    status_contactType("常用联系人","01"),
    status_initStatus("初始状态","01"),
    status_mpUser("中台用户","00"),
    status_facUser("工厂用户","01"),
    status_read("已读","1"),
    status_notread("未读","0");
    private String status;
    private String code;



    private StatusEnum(String status, String code) {
        this.status = status;
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
