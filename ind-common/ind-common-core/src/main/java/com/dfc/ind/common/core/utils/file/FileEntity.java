package com.dfc.ind.common.core.utils.file;

import lombok.Data;


@Data
public class FileEntity
{

	/** 视频类型 */
	private String type;
	/** 视频大小 */
	private String size;
	/** 视频路径 */
	private String path;
	/** 原文件名 */
	private String titleOrig;
	/** 新文件名 */
	private String titleAlter;
	/** 返回消息 */
	private String msg;
	/** 返回状态 */
	private int code;

}
