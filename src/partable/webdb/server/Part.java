package partable.webdb.server;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.simpleframework.xml.core.Persist;

import partable.webdb.server.xml.Subpart;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class Part {
	
	@PrimaryKey
	@Persistent
	public String name;

	@Persistent
	public String description;

	@Persistent
    public String type;

	@Persistent
	public String author;

    @Persistent
    public Date dateCreated;
    
    @Persistent
    public String nickname;
    
    @Persistent
    public String group;
    
    @Persistent
    public Set<String> categories;
    
    @Persistent
    public Text xmlData;
    
    @Persistent
    public Text sequence;
    
    @Persistent
    public String results;
    
    @Persistent
    public Set<String> subparts = new HashSet<String>(); 

    @Persistent
    public Set<String> allSubparts = new HashSet<String>(); 
    
    // Text search words
    @Persistent
    public Set<String> tswords = new HashSet<String>();
    
    
	public Part() {
    }
	
	public void addCategory(String c) {
		if (categories == null)
			categories = new HashSet<String>();
		categories.add(c);
	}

	public Key getKey() {
		return KeyFactory.createKey(Part.class.getSimpleName(), name) ;
	}
	
	public String wikiUrl() {
		return "http://partsregistry.org/wiki/index.php?title=Part:" + name;		
	}

	public String getJSON(boolean inclSeq) {
		StringBuilder json = new StringBuilder();

		json.append("{\n");
		json.append("\"name\":" + Util.quoteJsonString(name) + ",\n");
		json.append("\"desc\":" + Util.quoteJsonString(description) + ",\n");
		json.append("\"nickname\":" + Util.quoteJsonString(nickname) + ",\n");
		json.append("\"type\":" + Util.quoteJsonString(type) + ",\n");
		if (group!=null)
			json.append("\"group\":" + Util.quoteJsonString(group) + ",\n");
		json.append("\"author\":" + Util.quoteJsonString(author) + ",\n");
		if (inclSeq)
			json.append("\"sequence\":" + Util.quoteJsonString(sequence.getValue().replaceAll("\\s", "")) + ",\n");
		json.append("\"categories\":" + Util.stringHashToJson(categories) + ",\n");
		json.append("\"subparts\": " + Util.stringHashToJson(subparts) + ",\n");
		json.append("\"allSubparts\": " + Util.stringHashToJson(allSubparts) + "\n");
		json.append("}\n");
		
		return json.toString();
	}

	public void setByXMLPart(partable.webdb.server.xml.Part src) {
		description = src.shortDesc;
		author = src.author;
		results = src.results;
		type = src.type;
		nickname = src.nickname;
		subparts = new HashSet<String>();
		for(Subpart sb : src.subparts)
			subparts.add(sb.name);
		allSubparts = new HashSet<String>();
		for(Subpart sb : src.allSubparts)
			allSubparts.add(sb.name);
		
		String seq = "";
		if (src.sequences!= null) {
			for (String s : src.sequences)
				seq+=s;
		}
		sequence = new Text(seq.replace("\\s", ""));
	}


}
