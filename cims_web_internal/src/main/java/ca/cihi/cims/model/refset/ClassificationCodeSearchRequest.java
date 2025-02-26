package ca.cihi.cims.model.refset;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * 
 * Refset Picklist Classification Code Search Request.
 *
 */
public class ClassificationCodeSearchRequest extends BaseSerializableCloneableObject {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 8901237L;

    /**
     * Classification Code - either 'ICD-10-CA' or 'CCI'.
     */
    private String classificationCode;

    /**
     * Context Id.
     */
    private Long contextId;

    /**
     * Searchable Concept Code.
     */
    private String searchConceptCode;

    /**
     * Maximum # of Concepts returned.
     */
    private int maxResults;

    public String getClassificationCode() {
        return classificationCode;
    }

    public Long getContextId() {
        return contextId;
    }

    public String getSearchConceptCode() {
        return searchConceptCode;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setClassificationCode(String classificationCode) {
        this.classificationCode = classificationCode;
    }

    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }

    public void setSearchConceptCode(String searchConceptCode) {
        this.searchConceptCode = searchConceptCode;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((classificationCode == null) ? 0 : classificationCode.hashCode());
        result = prime * result + ((contextId == null) ? 0 : contextId.hashCode());
        result = prime * result + ((searchConceptCode == null) ? 0 : searchConceptCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClassificationCodeSearchRequest other = (ClassificationCodeSearchRequest) obj;
        if (classificationCode == null) {
            if (other.classificationCode != null)
                return false;
        } else if (!classificationCode.equals(other.classificationCode))
            return false;
        if (contextId == null) {
            if (other.contextId != null)
                return false;
        } else if (!contextId.equals(other.contextId))
            return false;
        if (searchConceptCode == null) {
            if (other.searchConceptCode != null)
                return false;
        } else if (!searchConceptCode.equals(other.searchConceptCode))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ClassificationCodeSearchRequest [classificationCode=" + classificationCode + ", contextId=" + contextId
                + ", searchConceptCode=" + searchConceptCode + ", maxResults=" + maxResults + "]";
    }
}
