/**
 * Created by wk_51920 on 2014/12/4.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        System.out.println("建立并等待连接……");
        ServerSocket serverSocket = new ServerSocket(5500);  //指定服务器要监听的端口
        Socket connectToClient = null;
        while (true) {
            connectToClient = serverSocket.accept(); //监听5500端口
            new ServerThread(connectToClient);
    }
    }
}