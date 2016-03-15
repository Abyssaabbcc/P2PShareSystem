
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;


public class ServerThread extends Thread {

    private Socket connectToClient;
    private ObjectInputStream inFromClient;
    private ObjectOutputStream outToClient;
    private UserInfo userInfo;
    private FileShare fileShare;
    private SearchResult searchResult;
    private Object object;

    public ServerThread(Socket socket) {
        System.out.println("启动了线程");
        connectToClient = socket;
        try {
            outToClient = new ObjectOutputStream(connectToClient.getOutputStream());
            inFromClient = new ObjectInputStream(connectToClient.getInputStream());
        } catch (IOException e) {
            System.err.println(e);
        }
        start();
    }

    private int loginCheck() {
        int flag = 3;              //表示出现数据库异常
        Connection connection = SQLConnection.connectSQL();
        Statement statement;
        String updateUserInfo = "UPDATE userInfo SET online=1 WHERE userName='" + userInfo.getUserName() + "'";
        String select = "SELECT * FROM userInfo WHERE userName='" + userInfo.getUserName() + "' AND userPassword='" + userInfo.getPassword() + "'";
        ResultSet resultSet;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(select);
            if (resultSet.next())          //结果集为空，即没有匹配的用户名和密码
            {
                if (resultSet.getInt(3) == 0) {
                    flag = 1;                   //用户名密码存在，可以登录
                    userInfo.setOnline(true);   //将用户状态设为在线
                    statement.executeUpdate(updateUserInfo);
                } else flag = 2;                   //用户密码存在，但已登录
            } else {
                flag = 0;                   //用户名密码不匹配
            }
        } catch (SQLException e) {
            System.out.println("loginCheck SQLException");
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("登录SQL异常");
            }
            return flag;
        }
    }

    private int registerCheck() {
        int flag = 2;              //2表示数据库未连接成功
        Connection connection = SQLConnection.connectSQL();
        Statement statement;
        String insertUser = "INSERT INTO userInfo(userName,userPassword,online) VALUES(?,?,?)";
        String select = "SELECT userName FROM userInfo WHERE userName='" + userInfo.getUserName() + "'";
        ResultSet resultSet;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(select);
            if (!resultSet.next())  //结果为空
            {
                flag = 0;               //此用户不存在，可以注册
                PreparedStatement ps = connection.prepareStatement(insertUser);
                ps.setString(1, userInfo.getUserName());
                ps.setString(2, userInfo.getPassword());
                ps.setInt(3, 0);
                int result = ps.executeUpdate();
            } else flag = 1;        //该用户已存在，注册失败
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            return flag;
        }
    }

    public void run() {
        System.out.println("正在运行中:");
        try {
            object = inFromClient.readObject();   //从套接字中读取用户信息对象
            if (object instanceof UserInfo) {
                userInfo = (UserInfo) object;
                if (userInfo.getFlag() == 1) {
                    int flag = loginCheck();
                    if (flag == 1) {
                        outToClient.writeObject(new ResultFlag(3));   //登录成功
                        outToClient.flush();
                    } else if (flag == 0) {
                        outToClient.writeObject(new ResultFlag(4));   //登录失败
                        outToClient.flush();
                    } else if (flag == 2) {
                        outToClient.writeObject(new ResultFlag(7));   //已有此用户登录
                        outToClient.flush();
                    }
                } else if (userInfo.getFlag() == 2) {
                    if (registerCheck() == 0) {
                        outToClient.writeObject(new ResultFlag(1));   //注册成功
                        outToClient.flush();
                    } else {
                        outToClient.writeObject(new ResultFlag(2));   //注册失败
                        outToClient.flush();
                    }
                } else if (userInfo.getFlag() == 3) {   //用户下线时的事务处理
                    Connection connection = SQLConnection.connectSQL();
                    Statement statement;
                    String updateUserInfo = "UPDATE userInfo SET online=0,isTransport=0 WHERE userName='" + userInfo.getUserName() + "'";
                    try {
                        statement = connection.createStatement();
                        statement.executeUpdate(updateUserInfo);
                    } catch (SQLException e) {
                        System.out.println("loginCheck SQLException");
                    }
                } else if (userInfo.getFlag() == 4) {   //设置用户传输状态
                    Connection connection = SQLConnection.connectSQL();
                    Statement statement;
                    String setTransportTrue = "UPDATE userInfo SET isTransport=1 WHERE userName='" + userInfo.getUserName() + "'";
                    String setTransportFalse = "UPDATE userInfo SET isTransport=0 WHERE userName='" + userInfo.getUserName() + "'";
                    try {
                        statement = connection.createStatement();
                        if (userInfo.isTransport())
                            statement.executeUpdate(setTransportTrue);
                        else statement.executeUpdate(setTransportFalse);
                        outToClient.writeObject(new ResultFlag(7));
                        outToClient.flush();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("loginCheck SQLException");
                    }
                }
            } else if (object instanceof FileShare) {
                fileShare = (FileShare) object;
                if (fileShare.getFunction() == 1) {
                    Connection connection = SQLConnection.connectSQL();
                    Statement statement;
                    String convert = fileShare.getFilePath().replaceAll("\\\\", "\\\\\\\\");  //用于将文件路径中的反斜杠转义
                    String select = "SELECT * FROM fileshare WHERE filePath='" + convert + "'";
                    String selectPort = "SELECT userPort FROM fileshare";

                    ResultSet resultSet1;
                    int maxPort = 2000;
                    try {
                        statement = connection.createStatement();
                        resultSet1 = statement.executeQuery(selectPort);
                        while (resultSet1.next()) {
                            int selectedPort = resultSet1.getInt("userPort");
                            System.out.println(selectedPort);
                            if (selectedPort > maxPort)
                                maxPort = selectedPort;
                        }
                        maxPort++;
                        ResultSet resultSet = statement.executeQuery(select);
                        if (resultSet.next()) {
                            outToClient.writeObject(new ResultFlag(5));   //该文件已经上传！
                            outToClient.flush();
                        } else {
                            String insertUser = "INSERT INTO fileshare(fileName,fileSize,userName,IPaddress,userPort,filePath) VALUES('" + fileShare.getFileName() + "','" + fileShare.getFileSize() + "','" + fileShare.getUserName() + "','" + fileShare.getIPaddress() + "','" + maxPort + "','" + convert + "')";
                            statement.executeUpdate(insertUser);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("插入上传文件信息失败！");
                    }
                } else if (fileShare.getFunction() == 2) { //此文件信息用于删除某一项
                    Connection connection = SQLConnection.connectSQL();
                    Statement statement;
                    String convert = fileShare.getFilePath().replaceAll("\\\\", "\\\\\\\\");  //用于将文件路径中的反斜杠转义
                    String select = "SELECT * FROM fileshare WHERE filePath='" + convert + "'";
                    String deleteUser = "DELETE FROM fileshare WHERE filePath='" + convert + "'";
                    ResultSet resultSet;
                    try {
                        statement = connection.createStatement();
                        resultSet = statement.executeQuery(select);
                        if (resultSet.next()) {
                            outToClient.writeObject(new ResultFlag(6));   //该文件已经上传，可以删除
                            outToClient.flush();
                            statement.executeUpdate(deleteUser);
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("插入上传文件信息失败！");
                    }
                } else if (fileShare.getFunction() == 3) //查询用户传输状态
                {
                    Connection connection = SQLConnection.connectSQL();
                    Statement statement;

                    String select = "SELECT isTransport FROM userinfo WHERE userName='" + fileShare.getUserName() + "'";
                    ResultSet resultSet;
                    try {
                        statement = connection.createStatement();
                        resultSet = statement.executeQuery(select);
                        if (resultSet.next()) {
                            if (resultSet.getInt("isTransport") == 1)
                                fileShare.setIsTransport(1);
                            else fileShare.setIsTransport(0);
                            outToClient.writeObject(fileShare);
                            outToClient.flush();
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("插入上传文件信息失败！");
                    }
                }
            } else if (object instanceof SearchResult) { //对查询结果进行操作
                searchResult = (SearchResult) object;
                if (searchResult.getFlag() == 1) {  //按用户名查找
                    Connection connection = SQLConnection.connectSQL();
                    Statement statement;
                    String select = "SELECT * FROM fileshare WHERE userName='" + searchResult.getSearchUser() + "'";
                    List<FileShare> resultList = new LinkedList<FileShare>();
                    ResultSet resultSet;
                    try {
                        statement = connection.createStatement();
                        resultSet = statement.executeQuery(select);
                        while (resultSet.next()) {   //将符合条件的每个文件对象都加入队列中
                            FileShare fileShare1 = new FileShare(resultSet.getString(1), resultSet.getLong(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(6), 3);
                            fileShare1.setPort(resultSet.getInt("userPort"));
                            resultList.add(fileShare1);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("插入上传文件信息失败！");
                    }
                    searchResult.setResult(resultList);
                    outToClient.writeObject(this.searchResult);   //将查询结果的链表发往客户端
                    outToClient.flush();
                } else if (searchResult.getFlag() == 2) {  //按文件名查找
                    Connection connection = SQLConnection.connectSQL();
                    Statement statement;
                    String select = "SELECT * FROM fileshare WHERE fileName='" + searchResult.getSearchUser() + "'";
                    List<FileShare> resultList = new LinkedList<FileShare>();
                    ResultSet resultSet;
                    try {
                        statement = connection.createStatement();
                        resultSet = statement.executeQuery(select);
                        while (resultSet.next()) {   //将符合条件的每个文件对象都加入队列中

                            FileShare fileShare1 = new FileShare(resultSet.getString(1), resultSet.getLong(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(6), 3);
                            fileShare1.setPort(resultSet.getInt("userPort"));
                            resultList.add(fileShare1);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("插入上传文件信息失败！");
                    }
                    searchResult.setResult(resultList);
                    outToClient.writeObject(this.searchResult);   //将查询结果的链表发往客户端
                    outToClient.flush();
                } else if (searchResult.getFlag() == 3) {  //将当前数据库中的所有内容发给客户端
                    Connection connection = SQLConnection.connectSQL();
                    Statement statement;
                    String select = "SELECT * FROM fileshare";
                    List<FileShare> resultList = new LinkedList<FileShare>();
                    ResultSet resultSet;
                    try {
                        statement = connection.createStatement();
                        resultSet = statement.executeQuery(select);
                        while (resultSet.next()) {   //将符合条件的每个文件对象都加入队列中
                            FileShare fileShare1 = new FileShare(resultSet.getString(1), resultSet.getLong(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(6), 3);
                            fileShare1.setPort(resultSet.getInt("userPort"));
                            resultList.add(fileShare1);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("插入上传文件信息失败！");
                    }
                    searchResult.setResult(resultList);
                    outToClient.writeObject(this.searchResult);   //将查询结果的链表发往客户端
                    outToClient.flush();
                }else if(searchResult.getFlag()==4)
                {
                    Connection connection = SQLConnection.connectSQL();
                    Statement statement;
                    String select = "SELECT * FROM fileshare WHERE userName='"+searchResult.getSearchUser()+"'";
                    List<FileShare> resultList = new LinkedList<FileShare>();
                    ResultSet resultSet;
                    try {
                        statement = connection.createStatement();
                        resultSet = statement.executeQuery(select);
                        while (resultSet.next()) {   //将符合条件的每个文件对象都加入队列中
                            FileShare fileShare1 = new FileShare(resultSet.getString(1), resultSet.getLong(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(6), 3);
                            fileShare1.setPort(resultSet.getInt("userPort"));
                            resultList.add(fileShare1);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("插入上传文件信息失败！");
                    }
                    searchResult.setResult(resultList);
                    outToClient.writeObject(this.searchResult);   //将查询结果的链表发往客户端
                    outToClient.flush();
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("用户信息未获得！");
        } catch (IOException e) {
            System.out.println("IO错误!");
        } finally {
            try {
                inFromClient.close();
                outToClient.close();
                connectToClient.close();
            } catch (IOException e) {
                System.out.println("通道未关闭！");
            }
        }
    }
}
