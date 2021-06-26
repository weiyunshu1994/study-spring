## 消息队列 | java简单实现

[toc]

## 1. 消息队列介绍：
- `消息队列`是MQ是一种系统间相互写作的通信机制

  

![在这里插入图片描述](https://weiyunshu1994-picgo-img.oss-cn-shenzhen.aliyuncs.com/Typora_20210620173556938.png)

- Broker：消息处理中心，负责消息的接收、存储、转发等；
- Producer：消息生产者，负责产生和发送消息到消息处理中心；
- Consumer：消息消费者，负责从消息处理中心获取消息，并进行相应的处理。

## 2. java设计一个简单的消息队列
其结构如下所示：



![在这里插入图片描述](https://weiyunshu1994-picgo-img.oss-cn-shenzhen.aliyuncs.com/Typora_20210620213430134.png)

### 2.1 消息处理中心
作为消息处理中心，至少有一个数据容器来保存接收到的消息。这里采用java中队列（Queue）的一个子类ArrayBockingQueue来实现。
如下是消息处理中心Broker的实现：
```java
import java.util.concurrent.ArrayBlockingQueue;

public class Broker {
     private final static int MAX_SIZE = 3;
     private static ArrayBlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(MAX_SIZE);

     public static void produce(String msg){
         if(messageQueue.offer(msg)){
             System.out.println("成功向消息处理中心投递消息： " + msg + ",当前缓存的消息数量是："+ messageQueue.size());
         } else{
             System.out.println("消息处理中心内暂存的消息达到最大负荷，不能继续放入消息！");
         }
         System.out.println("==============================");
     }

     public static String consume(){
         String msg = messageQueue.poll();
         if(msg != null){
             System.out.println("已经消费消息：" + msg + ",当前暂存的消息数量是：" + messageQueue.size());
         } else {
           System.out.println("消息处理中心内没有消息可供消费！");
         }
         System.out.println("==============================");
         return msg;
     }
}
```
有了消息处理中心类后，需要将该类的功能暴露出去，这样别人才能够用它来发送和接收消息。我们定义了BrokerServer类用来对外提供Broker类的服务。
```java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BrokerServer implements Runnable{
    
    public static int SERVICE_PORT = 9999;
    private final Socket socket;

    public BrokerServer(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try(
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream()))
        {
            while (true){
                String str = in.readLine();
                if (str == null){
                    continue;
                }
                System.out.println("接收到原始数据： " + str);
                if (str.equals("CONSUME")){
                    String message = Broker.consume();
                    out.println(message);
                    out.flush();
                }else {
                    Broker.produce(str);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        ServerSocket server = new ServerSocket(SERVICE_PORT);
        while(true){
            BrokerServer brokerServer = new BrokerServer(server.accept());
            new Thread(brokerServer).start();
        }
    }
}
```
在java中设计服务其功能的软件一般少不了套接字（Socket）和线程（Thread），需要通过线程的方式将应用启动起来，而服务器和应用的客户端需要用Socket进行网络通信。
### 2.2 客户端访问
有了消息处理中心服务器后，自然需要相应客户端来与之通信，来发送和接收消息。
```java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MyClient {
    public static void produce(String message) throws Exception{
        Socket socket = new Socket(InetAddress.getLocalHost(),BrokerServer.SERVICE_PORT);
        try(
                PrintWriter out = new PrintWriter(socket.getOutputStream())
                ){
            out.println(message);
            out.flush();
        }
    }

    public static String consume() throws Exception{
        Socket socket = new Socket(InetAddress.getLocalHost(),BrokerServer.SERVICE_PORT);
        try(
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream())
                ){
            out.println("CONSUME");
            out.flush();
            String message = in.readLine();
            return message;
        }
    }
}
```
以上是通用的客户端访问代码，接下来分别看一个生产消息和消费消息的示例：
**生产消息**：
```java
public class ProduceClient {
    public static void main(String[] args) throws Exception{
        MyClient client = new MyClient();
        client.produce("hello World.");
    }
}
```
**消费消息**：
```java

public class ConsumeClient {
    public static void main(String[] args) throws Exception{
        MyClient client = new MyClient();

        String message = client.consume();
        System.out.println("获得的消息为： " + message);
    }
}
```
### 2.3 运行效果
1. 开启BrokerServer服务

2. 生产消息：ProduceClient

3. 消费消息：ConsumeClient

  

![在这里插入图片描述](https://weiyunshu1994-picgo-img.oss-cn-shenzhen.aliyuncs.com/Typora_Typora_20210620215023941.png)
