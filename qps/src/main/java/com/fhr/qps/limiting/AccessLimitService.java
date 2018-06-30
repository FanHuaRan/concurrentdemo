package com.fhr.qps.limiting;

/**
 * Created by Huaran Fan on 2018/6/30
 *
 * @description:限流接口
 */
public interface AccessLimitService {
	/**
	 * 获取许可
	 * @return
	 */
	 boolean enter();

}
