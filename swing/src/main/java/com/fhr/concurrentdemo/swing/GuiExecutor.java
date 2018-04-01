package com.fhr.concurrentdemo.swing;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author FanHuaran
 * @description UI工作执行器
 * @create 2018-04-02 00:27
 **/
public class GuiExecutor extends AbstractExecutorService {
    private static  final  GuiExecutor GUI_EXECUTOR = new GuiExecutor();

    private   GuiExecutor(){

    }

    public static GuiExecutor getGuiExecutor() {
        return GUI_EXECUTOR;
    }

    @Override
    public void execute(Runnable command) {
        if(SwingUtilities.isEventDispatcherThread()){
            command.run();
        }else{
            SwingUtilities.invokeLater(command);
        }
    }


    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }
}
