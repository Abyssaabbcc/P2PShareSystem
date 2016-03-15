import java.io.Serializable;

/**
 * Created by wk_51920 on 2014/12/3.
 */
public class UserInfo implements Serializable {
    private String userName;
    private String password;
    private boolean online = false;   //表示此用户是否在线
    private boolean isTransport = false;        //表示此用户是否允许传输文件
    private static int portCount = 2000;
    private final int port = portCount++;              //此用户端口号
    private int flag;                 //1表示此信息用于登录，2表示此信息用于注册,3表示处理用户下线,4表示设置用户的传输状态

    public boolean isTransport() {
        return isTransport;
    }

    public void setTransport(boolean isTransport) {
        this.isTransport = isTransport;
    }




    public void setOnline(boolean online) {
        this.online = online;
    }


    public UserInfo(String userName, String password, int flag) {
        this.userName = userName;
        this.password = password;
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }


    public String getUserName() {
        return this.userName;
    }


    public String getPassword() {
        return this.password;
    }
}
