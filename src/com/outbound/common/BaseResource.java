package com.outbound.common;

import java.util.HashMap;
import java.util.Map;

import com.outbound.object.util.ResponseUtil;

/**
 * 公共resource部分
 * 
 * @author duanlsh
 *
 */
public class BaseResource {

	/**
	 * 合并列表和数量信息
	 * 
	 * @param list
	 * @param count
	 * @return
	 */
	public Map<String, Object> getMergeSumAndList(Object list, int count) {

		return getStateMergeSumAndList(list, count);
	}

	/**
	 * 合并列表和数量
	 * 
	 * @param list
	 * @param count
	 * @return
	 */
	public static Map<String, Object> getStateMergeSumAndList(Object list, int count) {
		Map<String, Object> map = new HashMap<>();
		map.put("count", count);
		map.put("list", list);
		return map;
	}

	/**
	 * 封装返回结果对象信息
	 * 
	 * @param codeNum
	 * @param message
	 * @param obj
	 * @return
	 */
	public ResponseUtil setResponseUtil(int codeNum, String message, Object obj) {
		ResponseUtil response = new ResponseUtil();
		response.setData(obj);
		response.setReturnCode(codeNum);
		response.setReturnMsg(message);
		return response;
	}
}
