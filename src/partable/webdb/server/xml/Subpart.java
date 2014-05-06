package partable.webdb.server.xml;

import org.simpleframework.xml.*;

@Root(name="subpart", strict=false)
public class Subpart {
	@Element(name="part_id", required=false)
	public String id;

	@Element(name="part_name", required=false)
	public String name;

	@Element(name="part_short_desc", required=false)
	public String shortDesc;
	
	@Element(name="part_type", required=false)
	public String type;
	
	@Element(name="part_nickname", required=false)
	public String nickname;
}
