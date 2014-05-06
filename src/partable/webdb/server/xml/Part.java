package partable.webdb.server.xml;

import java.util.List;

import org.simpleframework.xml.*;


@Root(name="part", strict=false)
public class Part
{
	@Element(name="part_id")
	public String id;
	
	@Element(name="part_name")
	public String name;

	@Element(name="part_short_name")
	public String shortName;

	@Element(name="part_short_desc", required=false)
	public String shortDesc;

	@Element(name="part_type", required=false)
	public String type;

	@Element(name="part_status", required=false)
	public String status;

	@Element(name="part_results", required=false)
	public String results;
	
	@Element(name="part_nickname", required=false)
	public String nickname;

	@Element(name="part_url")
	public String url;

	@Element(name="part_entered", required=false)
	public String entered;

	@Element(name="part_author", required=false)
	public String author;
	
	@Element(name="best_quality", required=false)
	public String bestQuality;
	
	@ElementList(entry="seq_data", name="sequences")
	public List<String> sequences;

	@ElementList(entry="category", name="categories")
	public List<String> categories;

	@ElementList(name="specified_subparts")
	public List<Subpart> subparts;

	@ElementList(name="deep_subparts")
	public List<Subpart> allSubparts;

	@ElementList(name="specified_subscars")
	public List<Subpart> subscars;
}
