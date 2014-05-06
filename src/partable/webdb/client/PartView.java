package partable.webdb.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HTML;

public class PartView extends Composite {

	public PartView() {
		setStyleName("partview-panel");
		
		TabPanel tabPanel = new TabPanel();
		initWidget(tabPanel);
		tabPanel.setSize("450px", "239px");

		FlowPanel mainPanel = new FlowPanel();
		tabPanel.add(mainPanel, "New tab", false);
		mainPanel.setSize("5cm", "3cm");
		
		Grid grid = new Grid(5, 2);
		mainPanel.add(grid);
		grid.setBorderWidth(1);
		grid.setSize("100%", "200px");
		
		Label label = new Label("Name:");
		grid.setWidget(0, 0, label);
		
		Label labName = new Label("");
		grid.setWidget(0, 1, labName);
		
		Label label_3 = new Label("Description");
		grid.setWidget(1, 0, label_3);
		
		Label labDesc = new Label("");
		grid.setWidget(1, 1, labDesc);
		
		Label label_4 = new Label("Group");
		grid.setWidget(2, 0, label_4);
		
		Label labGroup = new Label("TU Delft");
		grid.setWidget(2, 1, labGroup);
		
		Label label_1 = new Label("Author");
		grid.setWidget(3, 0, label_1);
		
		Label labAuthor = new Label("");
		grid.setWidget(3, 1, labAuthor);
		
		Label label_2 = new Label("Date added");
		grid.setWidget(4, 0, label_2);
		
		Label labDateAdded = new Label("");
		grid.setWidget(4, 1, labDateAdded);
		
		FlowPanel dnaPanel = new FlowPanel();
		tabPanel.add(dnaPanel, "New tab", false);
		dnaPanel.setSize("5cm", "3cm");
		
		HTML htmlDNAInfo = new HTML("DNA Info goes here", true);
		dnaPanel.add(htmlDNAInfo);
		
		TextBox textDNA = new TextBox();
		dnaPanel.add(textDNA);
		textDNA.setSize("100%", "");
	}
}
