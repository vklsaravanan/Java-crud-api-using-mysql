package com.recordManagement.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.recordManagement.model.Record;

import org.apache.catalina.connector.Response;

public class RecordDAO {
	
	private String jdbcURL = "jdbc:mysql://localhost:3306/mypcot";
    private String jdbcUsername = "root";
    private String jdbcPassword = "iamroot";
	
	private static final String INSERT_RECORD_SQL ="INSERT INTO `mypcot`.`records` (`user_id`, `title`, `description`, `category_id`, `status`, `created_at`, `updated_at`) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";


    private static final String SELECT_RECORD_BY_ID = "SELECT * FROM `mypcot`.`records` WHERE id = ? AND user_id = ?;";
//    private static final String SELECT_RECORD_BY_TITLE = "select * from `mypcot`.`records` where title =?";
    private static final String SELECT_ALL_RECORDS = "select * from `mypcot`.`records`  WHERE user_id = ?";
    private static final String DELETE_RECORD_SQL = "DELETE FROM `mypcot`.`records` WHERE `id` = ? ;";
    private static final String DELETE_MULTY_RECORD_SQL = "DELETE FROM `mypcot`.`records` WHERE `id` in ? ;";
//    private static final String UPDATE_RECORDS_SQL = "UPDATE `mypcot`.`records` SET `title` = ?, `description` = ?, `category_id` = ?, `status` = ?, `updated_at` = ? WHERE (`id` = ?);";

    public RecordDAO() {}
    
    protected Connection getConnection() {
        Connection connection = null;
        try {  
        	Class.forName("com.mysql.cj.jdbc.Driver");	
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }
    
    public int insertRecord(Record record) throws SQLException {
    	try (Connection connection = getConnection(); 
    		     PreparedStatement preparedStatement = connection.prepareStatement(INSERT_RECORD_SQL, Statement.RETURN_GENERATED_KEYS)) {
    		    preparedStatement.setLong(1, record.getUser_id());
    		    preparedStatement.setString(2, record.getTitle());
    		    preparedStatement.setString(3, record.getDescription());
    		    preparedStatement.setLong(4, record.getCategory_id());
    		    preparedStatement.setInt(5, record.isStatus()); // Assuming status is boolean

    		    int rowsAffected = preparedStatement.executeUpdate();

    		    if (rowsAffected > 0) {
    		        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
    		        if (generatedKeys.next()) {
    		            int generatedId = generatedKeys.getInt(1);
    		            System.out.println("Generated ID: " + generatedId);
    		            return generatedId;
    		        }
    		    }
    		    return 0;
    		} catch (SQLException e) {
    		    e.printStackTrace();
    		    return 0;
    		}
    }
    
    /*
     * return Record object which is matched record id and user id
     */
    public Record getRecord(int id,int user_id) {
        Record record = null;
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_RECORD_BY_ID);) {
            preparedStatement.setInt(2, user_id);
            preparedStatement.setInt(1, id);
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery(); 

            while (rs.next()) {
                String title = rs.getString("title");
                String description = rs.getString("description");
                int userId = rs.getInt("user_id");
                int categoryId = rs.getInt("category_id");
                boolean status = rs.getBoolean("status");
                Timestamp created_at = rs.getTimestamp("created_at");
                Timestamp updated_at = rs.getTimestamp("updated_at");
                
                record = new Record(id, userId, title, description,  categoryId, status, created_at, updated_at);
            }
        } catch (SQLException e) {
        	e.printStackTrace();
//            printSQLException(e);
        }
        return record;
    }
    
    /*
     * this function return records which is belongs to users
     */
    public List<Record> getAllRecords(int userId) {
        List<Record> records = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_RECORDS)) {
        	preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int user_id = rs.getInt("user_id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int category_id = rs.getInt("category_id");
                boolean status = rs.getBoolean("status");
                Timestamp created_at = rs.getTimestamp("created_at");
                Timestamp updated_at = rs.getTimestamp("updated_at");

                Record record = new Record(id, user_id, title, description, category_id, status, created_at, updated_at);
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    
    /*
     * delete specific record
     */
    public boolean deleteRecord(int id) throws SQLException {
        boolean rowDeleted = false;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(DELETE_RECORD_SQL);) {
            statement.setInt(1, id);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }
    
    public boolean deleteMultyRecord(int ids[]) throws SQLException {
        boolean rowDeleted = false;
        try (Connection connection = getConnection();) {
        	String id_values = ((Arrays.toString(ids).replace("[", "(").replace("]", ")")));
        	PreparedStatement statement = connection.prepareStatement(DELETE_MULTY_RECORD_SQL.replace("?", id_values));
        	System.out.println(statement);  
        	
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
        
    }
}