package com;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;

public class azureSQL {
    /*private static String hostName = "giaitienganh.database.windows.net";
    private static String dbName = "giaitienganh";
    private static String user = "khoinguyen1312";
    private static String password = "Kndapchai1312";
    private static String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);*/

    private static String hostName = "localhost";
    private static String dbName = "giaitienganh";
    private static String user = "sa";
    private static String password = "giaitienganh";
    private static String url = String.format("jdbc:sqlserver://%s;database=%s;user=%s;password=%s;", hostName, dbName, user, password);
    
    private static String tableName = "news2014";
    
    private static Connection connection;
    
    public static void init() throws SQLException {
    	connection = DriverManager.getConnection(url);
    }

    public static Map<String, Integer> querry(Set<String> keyWord) throws SQLException {
    	 Map<String, Integer> result = new HashMap<>();
    	 
    	 if (keyWord.isEmpty()) {
    		 return result;
    	 }
    	 
         StringBuilder builder = new StringBuilder();
         
         String query = "SELECT * FROM " + tableName + " WHERE ";
         
         builder.append(query);
         
         for (String key : keyWord) {
        	 if (builder.length() == query.length()) {
        		 builder.append("keyWord = '");
        		 builder.append(key.replace("'", "''"));
        		 builder.append("'");
        	 }
        	 else {
        		 builder.append(" OR keyWord = '");
        		 builder.append(key.replace("'", "''"));
        		 builder.append("'");
        	 }
         }
         
         query = builder.toString();
         

         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(query);
         
         while (resultSet.next())
         {
        	 String key = resultSet.getString("keyWord");
        	 int count = resultSet.getInt("countWord");
        	 
        	 result.put(key, count);
         }
         
         return result;
    }
}
