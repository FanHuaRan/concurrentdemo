package com.fhr.concurrentdemo.distributedlock;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 基于ZooKeeper实现的分布式锁 有顺序保证，
 * 无惊群效应（依靠顺序队列）
 * @author HuaRanFan
 * @since  2018年3月10日
 */
public class FairZooKeeperLock {
	private String zkQurom = "localhost:2181";

    private String lockName = "/mylock";

    private String lockZnode = null;

    private ZooKeeper zk;

    public FairZooKeeperLock(){
        try {
            zk = new ZooKeeper(zkQurom, 6000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    System.out.println("Receive event "+watchedEvent);
                    if(Event.KeeperState.SyncConnected == watchedEvent.getState())
                        System.out.println("connection is established...");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void ensureRootPath(){
        try {
            if (zk.exists(lockName,true)==null){
                zk.create(lockName,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取锁
     * @return
     * @throws InterruptedException
     */
    public void lock(){
        String path = null;
        ensureRootPath();
            try {
                path = zk.create(lockName+"/mylock_", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                lockZnode = path;
                List<String> minPath = zk.getChildren(lockName,false);
                System.out.println(minPath);
                Collections.sort(minPath);
                System.out.println(minPath.get(0)+" and path "+path);
                String watchNode = null;
                for (int i=minPath.size()-1;i>=0;i--){
                    if(minPath.get(i).compareTo(path.substring(path.lastIndexOf("/") + 1))<0){
                        watchNode = minPath.get(i);
                        break;
                    }
                }

                if (watchNode!=null){
                    final String watchNodeTmp = watchNode;
                    final Thread thread = Thread.currentThread();
                    Stat stat = zk.exists(lockName + "/" + watchNodeTmp,new Watcher() {
                        @Override
                        public void process(WatchedEvent watchedEvent) {
                            if(watchedEvent.getType() == Event.EventType.NodeDeleted){
                                thread.interrupt();
                            }
                            try {
                                zk.exists(lockName + "/" + watchNodeTmp,true);
                            } catch (KeeperException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    });
                    if(stat != null){
                        System.out.println("Thread " + Thread.currentThread().getId() + " waiting for " + lockName + "/" + watchNode);
                    }
                }
                try {
                    Thread.sleep(1000000000);
                }catch (InterruptedException ex){
                    System.out.println(Thread.currentThread().getName() + " notify");
                    System.out.println(Thread.currentThread().getName() + "  get Lock...");
                    return;
                }

            } catch (Exception e) {
               e.printStackTrace();
            }
    }

    /**
     * 释放锁
     */
    public void unlock(){
        try {
            System.out.println(Thread.currentThread().getName() +  "release Lock...");
            zk.delete(lockZnode,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

}
