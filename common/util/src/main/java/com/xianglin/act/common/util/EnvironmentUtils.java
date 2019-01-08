/**
 * 
 */
package com.xianglin.act.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 运行环境工具类
 * 
 * @author pengpeng 2015年9月22日上午10:16:52
 */
public class EnvironmentUtils {

	/** 常量 */
	public static final String ENV_PRD = "prd";

	/**
	 * 是否是生产环境
	 * 
	 * @param env
	 * @return
	 */
	public static boolean isPrdEnv(String env) {
		return StringUtils.equalsIgnoreCase(env, ENV_PRD);
	}

	/**
	 * 是否不是生产环境
	 * 
	 * @param env
	 * @return
	 */
	public static boolean isNotPrdEnv(String env) {
		return !isPrdEnv(env);
	}
}
