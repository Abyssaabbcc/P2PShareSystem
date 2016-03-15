import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wk_51920 on 2014/12/9.
 */
public class SearchResult implements Serializable {
    private String searchUser;

    public int getFlag() {
        return flag;
    }


    private int flag;   //1表示按用户名查找 2表示按文件名查找 3表示将数据库中所有文件信息显示 4表示将此用户的所有文件端口号和文件路径传输回来
    private List<FileShare> result = new LinkedList<FileShare>();

    public String getSearchUser() {
        return searchUser;
    }


    public SearchResult(String searchUser, int flag) {
        this.flag = flag;
        this.searchUser = searchUser;
    }

    public List<FileShare> getResult() {
        return result;
    }

    public void setResult(List<FileShare> result) {
        this.result = result;
    }

    public void removeElement(FileShare fileShare) {
        this.result.remove(fileShare);
    }
}
