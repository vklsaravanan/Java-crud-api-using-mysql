package com.recordManagement.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.recordManagement.dao.RecordDAO;
import com.recordManagement.model.Record;

import org.apache.catalina.connector.Response;

/**
 * Servlet implementation class RecordServlet
 */	
@WebServlet("/record/*")

public class RecordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private RecordDAO recordDAO = null;
	
    public void init() {
    	this.recordDAO = new RecordDAO();
    	System.out.println("record_servlet_initialized");
    }
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecordServlet() {
        super();
    }
    
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String pathInfo =  request.getPathInfo();
		String servletPath = request.getServletPath()+(pathInfo!=null ? pathInfo : "");
		String request_id = request.getHeader("request_id");
		ResponseData resData = new ResponseData(request_id, "failure");
		response.setContentType("application/json");
		
		if(servletPath.equals("/record")) {
			try {
				response.setStatus(Response.SC_BAD_REQUEST);
				BufferedReader body = request.getReader();
				Gson gson = new Gson();
				Record record = gson.fromJson(body, Record.class);
				response.setContentType("application/json");
				int new_record_id = recordDAO.insertRecord(record);
				if(new_record_id>0) {
					response.setStatus(Response.SC_OK);
					record.setId(new_record_id);
					resData.setData(record);
					resData.setMessage("record added successfully");
				}
				response.getWriter().write(new Gson().toJson(resData));
			} catch (Exception e) {
				e.printStackTrace();
				PrintWriter out = response.getWriter();
    	    	out.print(resData);
			}			
		}else {
			response.getWriter().print(resData);
	    	return;
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo =  request.getPathInfo();
		String servletPath = request.getServletPath()+(pathInfo!=null ? pathInfo : "");
		String request_id = request.getHeader("request_id");
		
		System.out.println(servletPath); 
		
		response.setContentType("application/json");
		
		if (servletPath.startsWith("/record/")) {

			String numberPart = servletPath.substring("/record/".length()).strip();

            try {
                int id = Integer.parseInt(numberPart);
                String resData = this.getRecord(id, request, response);
               
                if(resData ==  null) {
                	response.setStatus(Response.SC_NOT_FOUND);
                	response.getWriter().print(new Gson().toJson(new ResponseData(request_id, "failure")));
                	return;
                }else { 
        	    	response.getWriter().print(resData);
                }
            } catch (NumberFormatException e) {
            	String resData = this.getAllRecords(request);
            	if(resData ==  null) {
                	response.setStatus(Response.SC_NOT_FOUND);
                	response.getWriter().print(new Gson().toJson(new ResponseData(request_id, "failure")));
                	return;
	            }else { 
	            	// record is found
	    	    	PrintWriter out = response.getWriter();
	    	    	out.print(resData);
	    	    	return;
	            }
            }
        } else if (servletPath.equals("/record/") || servletPath.equals("/record")) {
        	String resData = this.getAllRecords(request);
        	if(resData ==  null) {
        		
            	response.setStatus(Response.SC_NOT_FOUND);
            	response.getWriter().print(new Gson().toJson(new ResponseData(request_id, "failure")));
            	return;
            	
            }else { 
            	// record is found
    	    	PrintWriter out = response.getWriter();
    	    	out.print(resData);
    	    	return;
            }
        } else { 
        	response.setStatus(Response.SC_NOT_ACCEPTABLE);
        	PrintWriter out = response.getWriter();
        	out.print(new Gson().toJson(new ResponseData(request_id, "failure")));
        	return;
		}
	}
	
	
	/*
	 * this function will delete the record if the request contain user id with token key
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo =  request.getPathInfo();
		String servletPath = request.getServletPath()+(pathInfo!=null ? pathInfo : "");
		
		ResponseData resData = new ResponseData(request.getHeader("request_id"), "record not deleted or not exist");
		response.setContentType("application/json");

		Gson gson = new Gson();
		
		if (servletPath.startsWith("/record/")) {
			String numberPart = servletPath.substring("/record/".length());
            try {
                int id = Integer.parseInt(numberPart);
                boolean delete_status = recordDAO.deleteRecord(id);
                Record rec = new Record(Integer.parseInt(numberPart));
                if(delete_status) {
                	response.setContentType("application/json");
                	resData.setData(rec);
                	resData.setMessage("record success fully deleted");
                	PrintWriter out = response.getWriter();  
        	    	out.print(gson.toJson(resData));
        	    	out.close();
        	    	return;
                	
                }else {
                	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                	response.setContentType("application/json");
        	    	PrintWriter out = response.getWriter();
        	    	out.print(gson.toJson(resData));
        	    	out.close();
                }
            } catch (NumberFormatException | SQLException e) {
            	response.setStatus(Response.SC_BAD_REQUEST);
            	PrintWriter out = response.getWriter();
    			out.print(gson.toJson(resData));
    			return;
            }
		} 
		
		// for multy delete
		else if(servletPath.equals("/record")){
			 	
			try {
				BufferedReader reader = request.getReader();
	            int[] data = gson.fromJson(reader, int[].class);
	            
	            boolean delete_status = recordDAO.deleteMultyRecord(data);
		       
	            if(delete_status) {
                	response.setContentType("application/json");
                	resData.setMessage("records success fully deleted");
                	PrintWriter out = response.getWriter();  
        	    	out.print(gson.toJson(resData));
        	    	out.close();
        	    	return;
                }else {
                	response.setStatus(Response.SC_BAD_REQUEST);
                	response.setContentType("application/json");
        	    	PrintWriter out = response.getWriter();
        	    	out.print(gson.toJson(resData));
        	    	out.close();
        	    	return;
                }
		    } catch (IOException | SQLException e) {
		    	response.setStatus(Response.SC_BAD_REQUEST);
            	PrintWriter out = response.getWriter();
    			out.print(gson.toJson(resData));
    			return;
		    }
		}
	}
	
	/* 
	 * this function will update the record if the request contain user id with token key
	 */
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setStatus(202);
	}	
	
	protected String getRecord(int id,HttpServletRequest req, HttpServletResponse resp) {
		Record record =	recordDAO.getRecord(id, req.getIntHeader("user_id"));
    	if(record == null)return null;
    	ResponseData responseData = new ResponseData(req.getHeader("request_id"), record, "success");
    	
    	Gson gs = new Gson(); 
    	 
    	String json = gs.toJson(responseData);
		return json;
	}
	
	protected String getAllRecords(HttpServletRequest req) {
		List<Record> records =	recordDAO.getAllRecords(req.getIntHeader("user_id"));
		ResponseData responseData = null;
    	if(records == null) {return null;};
    	 responseData = new ResponseData(req.getHeader("request_id"), records, "success");
    	
    	Gson gs = new Gson(); 
    	
    	String json = gs.toJson(responseData);
		return json;
	}
}


class ResponseData{
	
	private String request_id = null;
	private Record data = null;
	List<Record> datas = null;
	String message = null;

	public void setMessage(String message) {
		this.message = message;
	}
	//for individual record
	public ResponseData(String request_id, Record data, String message) {
		super();
		this.request_id = request_id;
		this.data = data;
		this.message = message;
	}
	//for bulk record
	public ResponseData(String request_id, List<Record> datas, String message) {
		super();
		this.request_id = request_id;
		this.datas = datas;
		this.message = message;
	}
	// for failure reuquests
	public ResponseData(String request_id, String message) {
		super();
		this.request_id = request_id;
		this.message = message;
	}

	public String getRequest_id() {
		return request_id;
	}
	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}
	public Record getData() {
		return data;
	}
	public void setData(Record data) {
		this.data = data;
	}
}
