Dog Tcc Version  1.0-SNAPSHOT
框架代码：
    core 核心模块;
    spring mvc 拦截模块;
    zookeeper 消息模块;
    protostuff 序列化模块;
demo 代码：
    Server1 启动事务，
         调用Server2 [执行一个本地事务]
         调用Server3 [执行两个本地事务]
    目前性能： 每个事务单元，花费20ms 以内的时间。
               如果客户端用多线程方式运行，结果瞬间返回。
