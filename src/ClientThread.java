/**
 * Created by wk_51920 on 2014/12/6.
 */


import java.io.*;
import java.net.*;

public class ClientThread {
    private Socket connectToServer;            //用于连接服务器的Socket
    private ObjectInputStream inFromServer;      //从服务器读数字状态
    private ObjectOutputStream outToServer;    //发往服务器
    private Object object;
    private SearchResult searchResult;
    private FileShare fileShare;
    private int resultFlag;

    public int getResultFlag() {
        return resultFlag;
    }

    public ClientThread(InetAddress addr, int port, Object object) throws IOException {
        connectToServer = new Socket(addr, port);   //服务器的地址和端口号
        this.object = object;
        inFromServer = new ObjectInputStream(connectToServer.getInputStream());   //从服务器读入数据
        outToServer = new ObjectOutputStream(connectToServer.getOutputStream());  //向服务器发送数据

    }

    private void sendUserToServer() {     //将用户信息送往服务器
        try {
            outToServer.writeObject(this.object);
            outToServer.flush();
        } catch (IOException e) {
            System.out.println("用户信息传送失败！");
        }
    }

    public SearchResult getSearchResult() {
        return searchResult;
    }


    public FileShare getFileShare() {
        return fileShare;
    }


    public void run() {
        try {
            sendUserToServer();                    //将客户端用户信息发往服务器

            Object object = inFromServer.readObject();
            if (object instanceof ResultFlag)
                this.resultFlag = ((ResultFlag) object).getFlg();
            else if (object instanceof SearchResult)
                this.searchResult = (SearchResult) object;
            else if (object instanceof FileShare)
                this.fileShare = (FileShare) object;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                inFromServer.close();
                outToServer.close();
                connectToServer.close();
            } catch (IOException e) {

            }
        }
    }
}