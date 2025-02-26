package ca.cihi.cims.web.rule.refset;

import javax.servlet.http.HttpServletRequest;

import ca.cihi.cims.web.bean.refset.RefsetLightBean;

/**
 * 
 * @author lzhu
 *
 */
public interface PermissionRule {

	 /**
     * Not applicable refset access permission.
     */
    final static String REFSET_ACCESS_PERMISSION_NA = "NA";

    /**
     * Writable refset access permission.
     */
    final static String REFSET_ACCESS_PERMISSION_WRITE = "WRITE";

    /**
     * Read-only refset access permission.
     */
    final static String REFSET_ACCESS_PERMISSION_READ = "READ";
    
    final static String REFSET_ACCESS_PERMISSION_Y = "Y";
    
    final static String REFSET_ACCESS_PERMISSION_N = "N";
    
    
    /**
     * Request Attribute - 'refset permission'.
     */
    final static String ATTR_REFSET_PERMISSION = "refsetPermission";
    
    /**
     * Request Attribute - 'write permission for lastest closed refset version'.
     */
    final static String ATTR_WRITE_FOR_LASTEST_CLOSED_VERSION = "writeForlastestClosedVersion";
    
    void applyRule(HttpServletRequest request, RefsetLightBean refsetLightBean);
}
