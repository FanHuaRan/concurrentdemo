package com.fhr.concurrentdemo.distributedlock.dynamicds;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author FanHuaran
 * @description MyBatis拦截器，这儿可以间接用来设置数据源
 * @create 2018-04-19 14:50
 **/
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class MyBatisDataSourceInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        mappedStatement.getSqlCommandType() == SqlCommandType
    }

    @Override
    public Object plugin(Object o) {
        // 创建代理对象
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 设置附加属性
    }
}
