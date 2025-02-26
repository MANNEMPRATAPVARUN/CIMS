package ca.cihi.cims.web.bean;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * Java bean that presents a key-value pair object. List of KeyValueBean are used in a view to populate drop down list,
 * check boxes, radio buttons 
 *
 */
public class KeyValueBean extends BaseSerializableCloneableObject {

    private static final long serialVersionUID = -5683056584967181079L;

    public KeyValueBean() {
        super();
    }

    public KeyValueBean(final String key, final String value) {
        super();
        this.key = key;
        this.value = value;
    }

    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "["
               + key
               + "="
               + value
               + "]";
    }
}
