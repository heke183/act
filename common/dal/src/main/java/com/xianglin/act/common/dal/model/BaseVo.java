/**
 * 
 */
package com.xianglin.act.common.dal.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 
 * 
 * @author wanglei 2016年8月12日上午11:28:49
 */
public class BaseVo implements Serializable{

	/**  */
	private static final long serialVersionUID = 2049925965374081566L;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
