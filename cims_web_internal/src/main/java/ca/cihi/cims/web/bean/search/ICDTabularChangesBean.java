package ca.cihi.cims.web.bean.search;

/**
 * ICD specific tabular changes bean
 * @author rshnaper
 *
 */
public class ICDTabularChangesBean extends TabularChangesBean {
	
	private static final long serialVersionUID = 1L;

	public ICDTabularChangesBean() {
		setLevel(HierarchyType.Category);
	}
}
