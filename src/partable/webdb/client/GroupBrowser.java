package partable.webdb.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.event.logical.shared.SelectionEvent;

public class GroupBrowser extends Composite {

	public GroupBrowser() {
		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
		initWidget(horizontalSplitPanel);
		
		Tree tree = new Tree();
		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			public void onSelection(SelectionEvent<TreeItem> event) {
				
			}
		});

		for (int i=0;i<10;i++)
			tree.addItem(Integer.toString(i));
		
		tree.setAnimationEnabled(true);
		horizontalSplitPanel.setLeftWidget(tree);
		tree.setSize("100%", "100%");
		
		FlowPanel flowPanel = new FlowPanel();
		horizontalSplitPanel.setRightWidget(flowPanel);
		flowPanel.setSize("100%", "100%");
		
		flowPanel.add(new PartView());
	}

}
