/**
 * Created by wk_51920 on 2014/12/13.
 */
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class TransportServer extends Thread{
    private FileShare fileShare;
    public TransportServer(FileShare fileShare) {
        this.fileShare=fileShare;
        System.out.println("Server4Doc start,Port"+fileShare.getPort());
    }
    public void run() {
        Socket s = null;
        try {
            ServerSocket ss = new ServerSocket(fileShare.getPort());
            while (true) {
                int length = 0;
                double sumL = 0 ;
                byte[] sendBytes = null;
                Socket socket = null;
                DataOutputStream dos = null;
                FileInputStream fis = null;
                boolean bool = false;
                try {
                    File file = new File(fileShare.getFilePath()); //要传输的文件路径
                    long l = file.length();
                    socket = ss.accept();

                    dos = new DataOutputStream(socket.getOutputStream());
                    fis = new FileInputStream(file);
                    sendBytes = new byte[1024];
                    while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                        sumL += length;
                        System.out.println("已传输："+((sumL/l)*100)+"%");
                        dos.write(sendBytes, 0, length);
                        dos.flush();
                    }
                    //虽然数据类型不同，但JAVA会自动转换成相同数据类型后在做比较
                    if(sumL==l){
                        bool = true;
                    }
                }catch (Exception e) {
                    System.out.println("客户端文件传输异常");
                    bool = false;
                    e.printStackTrace();
                } finally{
                    if (dos != null)
                        dos.close();
                    if (fis != null)
                        fis.close();
                    if (socket != null)
                        socket.close();
                }
            }
        }catch (Exception e) {
        }
    }
}
