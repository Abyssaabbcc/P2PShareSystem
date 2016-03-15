import java.io.Serializable;

/**
 * Created by wk_51920 on 2014/12/7.
 */
public class FileShare implements Serializable {
    private String fileName;    //文件名
    private long fileSize;    //文件大小
    private String userName;    //上传文件的用户名
    private String IPaddress;   //上传者的IP地址
    private String filePath;    //文件路径
    private int port;        //上传者的端口号
    private int isTransport; //该用户是否开启传输功能


    public int getIsTransport() {
        return isTransport;
    }

    public void setIsTransport(int isTransport) {
        this.isTransport = isTransport;
    }

    private int function;    //1表示此信息用于增加文件，2表示此文件用于删除文件, 3表示此文件信息用于从服务器传回查询结果,4表示查询此文件信息用于查询选中用户的传输状态
    private int displayFlag; //1只返回路径名 2返回所有信息


    public void setDisplayFlag(int displayFlag) {
        this.displayFlag = displayFlag;
    }

    public int getFunction() {
        return function;
    }

    public void setFunction(int function) {
        this.function = function;
    }

    public FileShare(String fileName, long fileSize, String userName, String IPaddress, String filePath, int function) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.userName = userName;
        this.IPaddress = IPaddress;
        this.filePath = filePath;
        this.function = function;
    }

    public String getFilePath() {
        return filePath;
    }



    public String getFileName() {
        return fileName;
    }


    public long getFileSize() {
        return fileSize;
    }


    public String getUserName() {
        return userName;
    }


    public String getIPaddress() {
        return IPaddress;
    }



    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String toString() {
        if (this.displayFlag == 1)
            return this.filePath;
        else return "文件名："+this.fileName+" 文件大小："+this.fileSize+" 上传用户："+this.userName+" 文件路径："+this.filePath;
    }
}
