package partable.webdb.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import partable.webdb.server.xml.PartXML;
import partable.webdb.server.xml.Subpart;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public class PartUpdater {
	private Logger log;

	PersistenceManager pm;
	Query partExistQuery, catExistQuery;
	
	public PartUpdater(PersistenceManager pm) {
		this.pm = pm;
		partExistQuery = pm.newQuery(Part.class);
		partExistQuery.setFilter("name == name_");
		partExistQuery.declareParameters("String name_");
		partExistQuery.compile();

		catExistQuery = pm.newQuery(Category.class);
		catExistQuery.setFilter("name == name_");
		catExistQuery.declareParameters("String name_");
		
		log = Util.log;
		if (log == null)
			log = Logger.getLogger(PartUpdater.class.getName());
	}
	
	static class FetchResult 
	{
		public FetchResult(Part part, Future<HTTPResponse> r, Future<HTTPResponse> infoResp) {
			this.part = part;
			response = r;
			this.infoResp = infoResp;
		}
		
		public Part part;
		public Future<HTTPResponse> response, infoResp ;

	}
	
	public void updateParts(List<String> partNames) {
		
		ArrayList<FetchResult> fetchResults = new ArrayList<FetchResult>(); 
		try {
			for(String partName : partNames) { 
				@SuppressWarnings("unchecked")
				List<Part> results = (List<Part>)partExistQuery.execute(partName);
				
				Part part = null;
				if (!results.isEmpty())
					part = results.get(0);
				else {
					part = new Part();
					part.name = partName;
				}
				
				URLFetchService urlfetch = URLFetchServiceFactory.getURLFetchService();
				
				try {
					String xmlurl = "http://partsregistry.org/xml/part." + partName; 
					String infourl = "http://partsregistry.org/cgi/partsdb/part_info.cgi?part_name=" + partName.replaceAll(" ", "%20");
					log.info("Downloading XML from " + xmlurl + "; InfoPage: " + infourl);
					
					Future<HTTPResponse> xmlResp = urlfetch.fetchAsync( new URL(xmlurl) );
					Future<HTTPResponse> infoResp = urlfetch.fetchAsync( new URL(infourl) );
					
					fetchResults.add( new FetchResult(part, xmlResp, infoResp) );
				} catch (MalformedURLException e) {
					log.warning(e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.warning(e.getMessage());
				}
			}
		} finally {
			partExistQuery.closeAll();
		}

		for (FetchResult fr : fetchResults) {
			try {
				HTTPResponse resp = fr.response.get();
				HTTPResponse infoResp = fr.infoResp.get();
				
				fr.part.group = getGroupFromWikiPage(new String(infoResp.getContent()));
				storePartFromXML(new String(resp.getContent()), fr.part);
				log.info(fr.part.name + " group: " + ((fr.part.group != null) ? fr.part.group : "?"));
			} catch (InterruptedException e) {
				log.warning(e.getMessage());
			} catch (ExecutionException e) {
				log.warning(e.getMessage());
			}
			
		}		
	}
	
	private String getGroupFromWikiPage(String content) {
		Pattern p = Pattern.compile("Designed by (.+) &nbsp;  Group:(.+) &nbsp;");
		Matcher m = p.matcher(content);
		
		while (m.find()) {
			int gc = m.groupCount();
			return m.group(2).trim();
		}
		return null;
	}

	private void storePartFromXML(String xml, Part part) {
		Serializer serializer = new Persister();
		try {
			PartXML pxml = serializer.read(PartXML.class, xml);
			
			if (pxml.partList.isEmpty())
				return;
			
			partable.webdb.server.xml.Part src = pxml.partList.get(0);
			part.xmlData = new Text(xml);
			part.setByXMLPart(src);
			
			part.categories = new HashSet<String>();
			initCategories(part, src);

			pm.makePersistent(part);
			pm.flush();
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
	}

	private void initCategories(Part part, partable.webdb.server.xml.Part src) {
		HashSet<String> partCategories = new HashSet<String>();
		
		for (String cat : src.categories) {
			String c = cat.substring(1);
			if (c.startsWith("//")) 
				c = c.substring(1); // in case of 3 slashes, remove one (Why 3 slashes anyway?)
			
			getOrCreateCategory(c, null);
			assignCategory(partCategories, c);
		}

		part.categories = partCategories;
	}

	private void assignCategory(HashSet<String> partCategories, String cat) {
		String cur = cat;
		
		while (true) {
			partCategories.add(cur);
			
			int p = cur.lastIndexOf('/');
			//if (cur.length() == 1 || p < 0) break;
			if(p <= 0) break; // dont add the root category, its redundant
			
			cur = cur.substring(0, Math.max(p,1));
		}
	}

	private Category getOrCreateCategory(String cat, Category subcat) {
		@SuppressWarnings("unchecked")
		List<Category> cats = (List<Category>) catExistQuery.execute(cat);
		Category c;
		
		if (cats.isEmpty()) {
			c = new Category();
			c.name = cat;
			pm.makePersistent(c);
		
			int catStart = cat.lastIndexOf('/');
			if (catStart >= 0) {
				String parent = cat.substring(0, Math.max(catStart, 1));
				getOrCreateCategory(parent, c);
			}
		} else {
			c = cats.get(0);
			// if it already exists, then the parent categories are also done already
		}
		if (subcat != null)
			c.addSubCategory(subcat.name);
		
		return c;
	}

	public void updatePartGroups(List<String> parts) {
		
	}

	public void updatePartCategory(Part part) {
		Serializer serializer = new Persister();
		PartXML pxml;
		try {
			pxml = serializer.read(PartXML.class, part.xmlData.getValue());
		} catch (Exception e) {
			log.warning("Failed to read known XML for " + part);
			return;
		}

		if (pxml.partList.isEmpty())
			return;

		partable.webdb.server.xml.Part src = pxml.partList.get(0);
		part.categories = new HashSet<String> ();
		initCategories(part, src);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		InputStream fs;
		try {
			fs = new FileInputStream("../crawler_/part.xml");
			
			Serializer serializer = new Persister();
			PartXML pxml = serializer.read(PartXML.class, fs);
			System.out.println("Parsed: " + pxml.partList.get(0).name);
			fs.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
