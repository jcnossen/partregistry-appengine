package partable.webdb.server.xml;

import java.util.ArrayList;

import org.simpleframework.xml.*;


@Root(name="rsbpml", strict=false)
public class PartXML {

	@ElementList(name="part_list")
	public ArrayList<Part> partList;
}
