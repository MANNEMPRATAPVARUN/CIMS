//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.09.06 at 06:40:12 PM PDT 
//


package org.cihi.claml.schema;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}RubricKind" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "rubricKind"
})
@XmlRootElement(name = "RubricKinds")
public class RubricKinds {

    @XmlElement(name = "RubricKind", required = true)
    protected List<RubricKind> rubricKind;

    /**
     * Gets the value of the rubricKind property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rubricKind property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRubricKind().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RubricKind }
     * 
     * 
     */
    public List<RubricKind> getRubricKind() {
        if (rubricKind == null) {
            rubricKind = new ArrayList<RubricKind>();
        }
        return this.rubricKind;
    }

}
