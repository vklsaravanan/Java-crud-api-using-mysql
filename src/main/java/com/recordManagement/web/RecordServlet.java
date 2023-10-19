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
import com.recordManagement.exceptions.RecordNotFoundException;
import com.recordManagement.model.Record;

import org.apache.catalina.connector.Response;

/**
 * Servlet implementation class RecordServlet
 */	
@WebServlet("/record/*")

public class RecordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String request_id = null;
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
    
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    	System.err.print(req.getMethod());
    	System.out.println(" - function called");
    	try {
    		this.request_id = req.getHeader("request_id");
    		if(this.request_id == null) {
    			throw new Exception("request_id required");
    		}
    	}catch(Exception e) {
    		res.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
    		ResponseData resData = new ResponseData(request_id, e.getMessage());
    		res.getWriter().print(new Gson().toJson(resData));
    	}
    	switch (req.getMethod()){
    		case "GET" : {
    			doGet(req, res);
    			break;
    		}
    		case "POST" : {
    			doPost(req, res);
    			break;
    		}
    		case "PUT" : {
    			doPut(req, res); 
    			break;
    		}
    		case "DELETE" : {
    			doPost(req, res);
    			break; 
    		}
    		default : {
    			
    		}
    	}
    }
    
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String pathInfo =  request.getPathInfo();
		String servletPath = request.getServletPath()+(pathInfo!=null ? pathInfo : "");
		ResponseData resData = new ResponseData(this.request_id, "failure");
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
		
		System.out.println(servletPath); 
		
		response.setContentType("application/json");
		
		if (servletPath.startsWith("/record/")) {

			String numberPart = servletPath.substring("/record/".length()).strip();
 
            try {
                int id = Integer.parseInt(numberPart);
                String resData = this.getRecord(id, request, response);
               
                if(resData ==  null) {
                	response.setStatus(Response.SC_NOT_FOUND);
                	response.getWriter().print(resData);
                	return;
                }else { 
        	    	response.getWriter().print(resData);
                }
            } catch (NumberFormatException e) {
            	String resData = this.getAllRecords(request);
            	if(resData ==  null) {
                	response.setStatus(Response.SC_NOT_FOUND);
                	response.getWriter().print(new Gson().toJson(new ResponseData(this.request_id, "failure")));
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
            	response.getWriter().print(new Gson().toJson(new ResponseData(this.request_id, "failure")));
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
        	out.print(new Gson().toJson(new ResponseData(this.request_id, "failure")));
        	return;
		}
	}
	
	
	/*
	 * this function will delete the record if the request contain user id with token key
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo =  request.getPathInfo();
		String servletPath = request.getServletPath()+(pathInfo!=null ? pathInfo : "");
		
		ResponseData resData = new ResponseData(this.request_id, "record not deleted or not exist");
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
	 * this function will UPDATE the record if the request contain user id with token key
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String pathInfo = request.getPathInfo();
	    String servletPath = request.getServletPath() + (pathInfo != null ? pathInfo : "");
	    ResponseData resData = new ResponseData(this.request_id, "failure");
	    response.setContentType("application/json");

	    if (!servletPath.equals("/record")) {
	        sendErrorResponse(response, resData, Response.SC_BAD_REQUEST, "Wrong method or wrong endpoint");
	        return;
	    }

	    try {
	        BufferedReader body = request.getReader();
	        Gson gson = new Gson();
	        Record record = gson.fromJson(body, Record.class);

	        if (record == null) {
	            sendErrorResponse(response, resData, Response.SC_BAD_REQUEST, "Invalid input data");
	            return;
	        }

	        Record updatedRecord = recordDAO.updateRecord(record);

	        if (updatedRecord != null) {
	        	resData.setData(record);
	        	sendSuccessResponse(response, resData, "Record successfully updated");
	        } else {
	            sendErrorResponse(response, resData, Response.SC_INTERNAL_SERVER_ERROR, "Failed to update the record");
	        }

	    } catch (RecordNotFoundException e) {
	        sendErrorResponse(response, resData, Response.SC_BAD_REQUEST, e.getMessage());
	    } catch (Exception e) {
	        sendErrorResponse(response, resData, Response.SC_INTERNAL_SERVER_ERROR, "Internal server error");
	    }
	}
	
	protected String getRecord(int id,HttpServletRequest req, HttpServletResponse resp) {
		Record record =	recordDAO.getRecord(id, req.getIntHeader("user_id"));
    	if(record == null)return null;
    	ResponseData responseData = new ResponseData(this.request_id, record, "success");
    	
    	Gson gs = new Gson(); 
    	 
    	String json = gs.toJson(responseData);
		return json;
	}
	
	protected String getAllRecords(HttpServletRequest req) {
		List<Record> records =	recordDAO.getAllRecords(req.getIntHeader("user_id"));
		ResponseData responseData = null;
		
    	if(records.isEmpty()) {return null;};
    	 responseData = new ResponseData(this.request_id, records, "success");
    	
    	Gson gs = new Gson(); 
    	
    	String json = gs.toJson(responseData);
		return json;
	}
	
	private void sendSuccessResponse(HttpServletResponse response, ResponseData resData, String message) throws IOException {
	    response.setStatus(Response.SC_OK);
	    resData.setMessage(message);
	    response.getWriter().print(new Gson().toJson(resData));
	}

	private void sendErrorResponse(HttpServletResponse response, ResponseData resData, int statusCode, String message) throws IOException {
	    response.setStatus(statusCode);
	    resData.setMessage(message);
	    response.getWriter().print(new Gson().toJson(resData));
	}
}


