package partable.webdb.server;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class Category {
	
	@PrimaryKey
	@Persistent
	public String name;
	
	@Persistent
	public Set<String> subCategories = new HashSet<String>(); 

	public void addSubCategory(String subcat) {
		if (subCategories == null)
			subCategories = new HashSet<String>();
		subCategories.add(subcat);
	}
	
}
