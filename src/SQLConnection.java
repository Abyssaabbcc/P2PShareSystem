/**
 * Created by wk_51920 on 2014/12/2.
 */
import java.sql.*;

public class SQLConnection {
    public static Connection connectSQL(){
        String user = "root";
        String passwd = "root";
        String url = "jdbc:mysql://localhost:3306/p2p";

        Connection connection = null;

        //注册驱动

        try {
            Class.forName("com.mysql.jdbc.Driver");

            //获取数据库连接
            connection = DriverManager.getConnection(url, user, passwd);



        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection connection,Statement sta,ResultSet resultSet){
        try {
            if(connection!=null){
                connection.close();
            }
            if(sta!=null){
                sta.close();
            }
            if(resultSet!=null){
                resultSet.close();
            }
        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

