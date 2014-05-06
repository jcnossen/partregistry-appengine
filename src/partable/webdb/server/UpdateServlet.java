package partable.webdb.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.log.Log;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;
import com.google.appengine.api.labs.taskqueue.TaskOptions.*;
import com.google.gwt.http.client.URL;

/**
 * @author jc
 *
 * Gets called by a cron-job to reload the part list
 */
public class UpdateServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(UpdateServlet.class.getName());
	
	public UpdateServlet() {
		Util.log = log;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setContentType("text/plain");
		PrintWriter w = resp.getWriter();
		String action = req.getParameter("action");

		if (action == null)
			action = "";
		
		if (action.equals("updatelist")) {
			PartListLoader pll = new PartListLoader();
			String content = PartListLoader.loadFile("http://partsregistry.org/das/parts/entry_points");
			pll.parsePartListXML(content);
			createNewPartTasks(pll.getPartList());
			w.write("OK: " + pll.getPartList().size() + " parts");
		} else if (action.equals("categories")) {
			handleUpdateCategories(req, resp);
		} else if (action.equals("crawlgroupoverview")) {
			handleCrawlOverviewPage(req,resp);			
		} else 
			w.write("What to do?");
	}

	private void handleCrawlOverviewPage(HttpServletRequest req,
			HttpServletResponse resp) throws IOException  {
		
		String overviewURL = req.getParameter("page");
		
		if (overviewURL == null)
			overviewURL = "http://ung.igem.org/Team_Parts?year=2010";
		else
			overviewURL = URLDecoder.decode(overviewURL, Charset.defaultCharset().name());
		
		log.info("Loading overview page from " + overviewURL);
		String content = PartListLoader.loadFile(overviewURL);
	
		if (content != null) {
			//Pattern p = Pattern.compile("http://partsregistry.org/cgi/partsdb/pgroup.cgi?pgroup=(.+?)&group=(.+?)");
			Pattern p = Pattern.compile("http://partsregistry.org/cgi/partsdb/pgroup\\.cgi\\?pgroup=(.+?)&group=(.+?)'");
			Matcher m = p.matcher(content);
			
			while (m.find()) {
				String url = m.group(0);
				url = url.substring(0,url.length()-1);
				log.info("Adding crawl task for: " + url);
				resp.getWriter().println("Adding crawl task for: " + url);
				
				QueueFactory.getDefaultQueue().add(
						url("/update").payload("crawlpartpage;"+url).method(Method.POST));
			}
		}
		
		resp.getWriter().println("OK");
	}

	private void handleCrawlPartListPage(String page)
	{
		PartListLoader pll = new PartListLoader();
		
		System.out.println("Crawl part list page: " + page); 
		String content = PartListLoader.loadFile(page);
		if (content != null) {
			pll.parsePartListHTML(content);
			createNewPartTasks(pll.getPartList());
		}
	}

	private void handleUpdateCategories(HttpServletRequest req,
			HttpServletResponse resp) 
	{
		int limit = 200;
		String startKey = req.getParameter("startkey");
		PersistenceManager pm = PMF.get();
		Query q = pm.newQuery(Part.class);
		q.setRange(0, limit);
		q.setOrdering("name");
		
		List<Part> parts;
		if (startKey != null) {
			q.setFilter("name > :1");
			parts = (List<Part>)q.execute(startKey);
		} else
			parts = (List<Part>)q.execute();

		log.info("Updating " + parts.size() + " parts.. " + (startKey != null ? "(> " + startKey + ")" : ""));
		
		PartUpdater pu = new PartUpdater(pm);
		
		for (Part p : parts)
			pu.updatePartCategory(p);
		PMF.release();
		
		String taskCounter = req.getParameter("taskcount");
		if (taskCounter == null)
			taskCounter = "0";
		int taskCount = Integer.parseInt(taskCounter);

		if (parts.size() == limit) {
			QueueFactory.getDefaultQueue().add(
					url("/update")
					.method(Method.GET)
					.param("action", "categories")
					.param("startkey", parts.get(limit-1).name)
					.param("taskcount", Integer.toString(taskCount+1)));
		} else log.info("Category update done");
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		char[] dst = new char[req.getContentLength()];
		req.getReader().read(dst);

		List<String> elems = Arrays.asList(new String(dst).split(";"));
		String action = "";
		if (!elems.isEmpty()) action = elems.get(0);

		if (action.equals("parts")) {
			List<String> parts = elems.subList(1, elems.size());
		//	log.log(Level.FINE, "Updating " + parts.size() + " parts..");
			
			PartUpdater pu = new PartUpdater(PMF.get());
			pu.updateParts(parts);
			PMF.release();
		}
		else if (action.equals("load_part_group")) {
			List<String> parts = elems.subList(1, elems.size());
			log.info("Updating groups of " + parts.size() + " parts..");
			
			PartUpdater pu = new PartUpdater(PMF.get());
			pu.updatePartGroups(parts);
			PMF.release();
		} 
		else if (action.equals("crawlpartpage")) {
			handleCrawlPartListPage(elems.get(1));
		}
	}

	private void createNewPartTasks(List<String> partList) {
		log.info("Creating tasks for " + partList.size() + " parts");
		
		Queue queue = QueueFactory.getQueue("updatelist");
		int i = 0;
		int taskMax = 5;

		while (i < partList.size()) {
			String partsubset = "parts;";
			int j = 0;
			
			while(i<partList.size() && j++<taskMax) {
				partsubset += partList.get(i) + ";";
				i++;
			}
		
			queue.add(url("/update").payload(partsubset));
		}
		
	}
	
	
}
