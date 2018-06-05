package com.fhr.concurrentdemo.distributedlock.dynamicds;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author FanHuaran
 * @description 动态数据源
 * @create 2018-04-19 15:04
 **/
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return null;
    }
}
