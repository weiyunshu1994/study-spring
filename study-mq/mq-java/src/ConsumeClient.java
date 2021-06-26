
public class ConsumeClient {
    public static void main(String[] args) throws Exception{
        MyClient client = new MyClient();

        String message = client.consume();
        System.out.println("获得的消息为： " + message);
    }
}
