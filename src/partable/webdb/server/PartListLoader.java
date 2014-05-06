package partable.webdb.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartListLoader {
	
	ArrayList<String> partList = new ArrayList<String>();

	public List<String> getPartList() {
		return partList;
	}
	
	public PartListLoader() {}
	
	public static String loadFile(String file) {
		URL url;
		try {
			url = new URL(file);
			InputStream s = url.openStream();
			String content = Util.streamToString(s);
			s.close();
			return content;
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	// DAS server XML
	public void parsePartListXML(String xml) {

		String[] lines = xml.split("\n");
		Util.log.info("parsePartListXML: " + lines.length + " lines of XML\n");
		
		Pattern p = Pattern.compile("<SEGMENT id='(.+?)'");
		
		int li = 1;
		for (String line : lines) {
			
			try {
				Matcher m = p.matcher(line);
				
				while (m.find()) {
					int gc = m.groupCount();
					String id = m.group(1);
					//System.out.println("Designer: " + m.group(1));
					this.partList.add(id);
				}
			} catch (Exception e) {
				Util.log.warning("Exception processing line " + li + ":" + e.getMessage() + "; \n" + line);
			}
			
			li++;
		}
	}

	public void parsePartListHTML(String content) {
		Pattern p = Pattern.compile("Part:(.+?)'");
		Matcher m = p.matcher(content);
		
		while (m.find()) {
			int gc = m.groupCount();
			String id = m.group(1);
			this.partList.add(id);
		}
	}
	
	public void outputList() {
		for (String s : partList)
			System.out.println("Part ID: " + s);
	}
	
	public static void main(String[] args) {
		String f = "http://partsregistry.org/cgi/partsdb/pgroup.cgi?pgroup=iGEM2010&group=TU_Delft";
		String c=loadFile(f);

		PartListLoader pll = new PartListLoader();
		pll.parsePartListHTML(c);
		pll.outputList();
	}
}
