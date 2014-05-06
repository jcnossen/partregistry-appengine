package partable.webdb.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QueryServlet extends HttpServlet {
	static class JsonRequestHandler {
		PrintWriter w;
		HttpServletRequest request;
		HttpServletResponse response;
		
		public JsonRequestHandler(HttpServletRequest req, HttpServletResponse resp) {
			request = req;
			response = resp;
		}
		
		void handle() throws IOException {
			String ctype = "text/plain";
			String jsonp = request.getParameter("jsonp");
			String type = request.getParameter("type");
			if (type == null) type = "parts";

			w = response.getWriter();
			if (jsonp != null) {
				ctype = "text/javascript";
				w.write(jsonp + "(");
			}

			response.setContentType(ctype);
			if (type.equals("parts"))
				handlePartQuery();
			else if(type.equals("cat"))
				handleCatQuery();
			else {
				w.write("{error: true, msg: 'Unknown query'}");
				response.setStatus(400);
			}
			
			if (jsonp!=null)
				w.write(");");
			
		}
		
		private void handleCatQuery() {
			PersistenceManager pm = PMF.get();
			Query q = pm.newQuery(Category.class);
			
			@SuppressWarnings("unchecked")
			List<Category> cats = (List<Category>) q.execute();
			
			w.write("[");
			for (Category c : cats)
				w.write(Util.quoteJsonString(c.name));
			w.write("]");
			
			q.closeAll();
			PMF.release();
		}

		private void handleXmlQuery() {
			String partName = request.getParameter("part");
			if (partName == null) {
				w.write("{error: true, msg: 'No part given'");
				response.setStatus(400); // Bad Request
				return;
			}
			
			PersistenceManager pm = PMF.get();
			Part part = pm.getObjectById(Part.class, partName);

//			Util.quoteJsonString(part.xmlData);
		}

		private void handlePartQuery() throws IOException {
			String param = request.getParameter("param");
			String value = request.getParameter("value");
			
			if (param != null && value != null) {
				PersistenceManager pm = PMF.get();
		
				Query q = pm.newQuery(Part.class);
				q.setFilter(param + " == :0");
				
				@SuppressWarnings("unchecked")
				List<Part> parts = (List<Part>) q.execute(value);
				q.closeAll();
				writePartList(parts);
				response.setContentType("application/json");
				PMF.release();
			}
		}

		private void writePartList(List<Part> parts)
			throws IOException
		{
			String seq = request.getParameter("sequence");
			boolean omitSeq = (seq != null) && seq.equals("0");
			
			w.write("[");
			boolean first = true;
			for (Part p : parts) {
				if (!first)
					w.write(",");
				w.write(p.getJSON(!omitSeq));
				first=false;
			}
			w.write("]");
		}
	}

	static class TextRequestHandler {
		PrintWriter w;
		HttpServletRequest request;
		HttpServletResponse response;
		
		public TextRequestHandler(HttpServletRequest req, HttpServletResponse resp) {
			request = req;
			response = resp;
		}
		
		void handle() throws IOException {
			String type = request.getParameter("type");
			if (type == null) type = "parts";

			w = response.getWriter();
			response.setContentType("text/plain");

			if (type.equals("parts"))
				handlePartQuery();
			else if (type.equals("partxml"))
				handleXmlQuery();
			else if (type.equals("cat"))
				handleCatQuery();
			else {
				w.write("error: Unknown query");
				response.setStatus(400);
			}
		}
		
		private void handleCatQuery() {
			PersistenceManager pm = PMF.get();
			Query q = pm.newQuery(Category.class);
			
			@SuppressWarnings("unchecked")
			List<Category> cats = (List<Category>) q.execute();
			
			for (Category c : cats)
				w.write(c.name +"\n");
			
			q.closeAll();
			PMF.release();
		}

		private void handleXmlQuery() {
			String partName = request.getParameter("part");
			if (partName == null) {
				w.write("error: No part");
				response.setStatus(400); // Bad Request
				return;
			}
			
			PersistenceManager pm = PMF.get();
			Part part = pm.getObjectById(Part.class, partName);
			w.write(part.xmlData.getValue());
			PMF.release();
		}

		private void handlePartQuery() throws IOException {
			String param = request.getParameter("param");
			String value = request.getParameter("value");
			
			if (param != null && value != null) {
				PersistenceManager pm = PMF.get();
		
				Query q = pm.newQuery(Part.class);
				q.setFilter(param + " == :0");
				
				@SuppressWarnings("unchecked")
				List<Part> parts = (List<Part>) q.execute(value);
				q.closeAll();
				writePartList(parts);	
				PMF.release();
			}
		}

		private void writePartList(List<Part> parts)
			throws IOException
		{
			String descParam = request.getParameter("desc");
			boolean haveDesc = false;
			if (descParam != null && descParam.equals("yes"))
				haveDesc = true;
			
			for(Part part : parts) {
				if (haveDesc)
					w.write(part.name + ";" + part.description + "\n");
				else
					w.write(part.name + "\n");
			}
		}
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI();
		
		if (uri.equals("/query/json"))
			new JsonRequestHandler(req, resp).handle();
		else if (uri.equals("/query/simple"))
			new TextRequestHandler(req, resp).handle();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String uri = req.getRequestURI();
		
		if (uri.equals("/query/json"))
			new JsonRequestHandler(req, resp).handle();
		else if (uri.equals("/query/simple"))
			new TextRequestHandler(req, resp).handle();
	}

}
