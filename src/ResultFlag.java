import java.io.Serializable;

/**
 * Created by wk_51920 on 2014/12/9.
 */
public class ResultFlag implements Serializable {
    private int flg; //1:用户注册成功，2：用户注册失败 3:用户登录成功 4：用户登录失败 5:文件在服务器端已存在（用于上传操作提示） 6：该文件已经上传，可以删除
                     // 7:该用户传输状态已成功修改

    public ResultFlag(int flg) {
        this.flg = flg;
    }

    public int getFlg() {
        return flg;
    }


}
