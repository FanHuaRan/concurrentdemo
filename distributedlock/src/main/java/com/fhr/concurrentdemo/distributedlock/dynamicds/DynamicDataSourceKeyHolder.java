package com.fhr.concurrentdemo.distributedlock.dynamicds;

/**
 * @author FanHuaran
 * @description 全局Holder，为MyBatis的拦截器和数据源提供一个间接的桥梁
 * @create 2018-04-19 15:06
 **/
public class DynamicDataSourceKeyHolder {
    // 数据源KEY的线程本地变量
    protected static final ThreadLocal<String> KEY_HOLDER = new ThreadLocal<>();

    // 设置主数据源KEY
    public static void setMaster() {
        KEY_HOLDER.set("MASTER");
    }

    // 设置从数据源KEY
    public static void setSlave() {
        // 这儿可以加上负载均衡的策略，从而能够使用一主多从策略，
        // 并且让读请求均摊到主库和从库上

        // 这儿暂时全部把读请求设置到SLAVE上
        KEY_HOLDER.set("SLAVE");
    }

    // 获取当前线程的数据源KEY
    public static String getDataSource() {
        return KEY_HOLDER.get();
    }
}
