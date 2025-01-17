package ca.cihi.cims.bean;

import java.util.ArrayList;
import java.util.List;


public class Utils {
	private static int counter = 1000;
	private static List<TreeBean>  database =new ArrayList<TreeBean>();;
	static{
		generateDatabase();
	}

	public static int getNextId() {
		return ++counter;
	}

	public static List<TreeBean> getChildrenData(String parentKey) {
		// (1) get this node
		TreeBean parentNode = null;
		for (TreeBean node : database) {
			if (node.getKey().equalsIgnoreCase(parentKey)) {
				parentNode = node;
				break;
			}
		}

		if (parentNode != null && parentNode.getLevel() < 3) {
			int childNum = (int) (Math.random() * 10.0);

			for (int i = 1; i <= childNum; i++) {
				TreeBean childNode = new TreeBean(parentNode.getTitle() + "."
						+ i);
				childNode.setLevel(parentNode.getLevel() + 1);
				parentNode.addChild(childNode);

				if (childNode.getLevel() < 3 && Math.random() < 0.5) {
					childNode.setFolder(true);
					childNode.setLazy(true);
				}
				database.add(childNode);
			}
			return parentNode.getChildren();
		} else {
			return new ArrayList<TreeBean>();
		}
	}

	public static void generateDatabase() {
		database.clear();
		for (int i = 1; i <= 10; i++) {
			// generate 10 root nodes
			TreeBean rootNode = new TreeBean(i + "");
			rootNode.setLevel(1);

				// 90% this root node chance has level 2 child node
				rootNode.setLazy(true);
				rootNode.setFolder(true);
				
			
			database.add(rootNode);

		}

	}

	public static List<TreeBean> getTreeRootNodes() {
		List<TreeBean> rootNodes = new ArrayList<TreeBean>();
		for(TreeBean node: database){
			if(node.getLevel()==1){
				rootNodes.add(node);
			}
		}
		return rootNodes;
	}
}
