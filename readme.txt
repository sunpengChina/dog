Dog Tcc Version  1.0-SNAPSHOT

     DogTcc是一个高速 高可用 分布式事务框架。

     DogTcc提供了最先进的设计框架，也许1.0-SNAPSHOT版本他的代码不是最优美的， 但是他的框架是最先进的。

感谢于他的框架设计，也许你不信，他比绝大多数商业产品要快。[20ms/事务 在你的电脑上或许更低]


Why So Fast ? 

     1 调用者间无链接             
     
     2 异步通知模型
     
     3 模块化设计
     
     4 核心模块和消息发送，消息发现解耦合，理论上支持一切协议
     
     5 支持事务结果的异步返回和同步返回     


Quickstart：
     1 下载zookeeper ，作者用的是：zookeeper-3.4.13 ， 进入 bin 目录，通过zkServer 启动zk服务。
     2 执行zk bin目录下的zkCli 。在客户端中执行 create /dog "dog"  ； 作为我们样例需要的工作空间。
     3 DownLoad 代码，在代码根目录 mvn clean install
     4 依次启动 eureka ,Server1,Server2,Server3 
     5 在浏览器中测试：   
         [为了体现框架性能，我们的任务只是简单打印数据，如果执行重的本地任务，本地任务时间过多，将远远大于框架执行时间，不利于观察框架所用时间]
         http://127.0.0.1:8081/chainTcc                一次链式事务调用，检验你的系统是否正常部署
         http://127.0.0.1:8081/noTcc1000               无事务调用，1000次的时间
         http://127.0.0.1:8081/singleTcc100            调用100次简单事务单元的时间
         http://127.0.0.1:8081/singleTcc1000thread     多线程调用100次简单事务单元的时间
         http://127.0.0.1:8081/chainTcc1000thread      多线程调用100次链式事务单元的时间
  
  

如果您运行了样例代码，那么恭喜您，您已经感受到了它的高速，那么下面您可以：
  
     1 提供issues ,为DogTcc 提供建议
     
     2 pull request ,如果您有时间为代码做改善的话
     
     3 加入我们的开发团队
     
     4 如果您有机会为DogTcc代码做恭喜，您将文章头Why So Fast中的内容
     
 
 其他问题：
 
     wiki: https://github.com/sunpengChina/dog/wiki
     
     请联系： sunpengchina85@gmail.com       


     

     
          
