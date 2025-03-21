//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.09.06 at 06:40:12 PM PDT 
//


package org.cihi.claml.schema;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for xhtml.rtc.type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xhtml.rtc.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{}xhtml.rt.content"/>
 *       &lt;attGroup ref="{}xhtml.rtc.attlist"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xhtml.rtc.type", propOrder = {
    "xhtmlInlNoRubyMix"
})
public class XhtmlRtcType {

    @XmlElementRefs({
        @XmlElementRef(name = "abbr", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "q", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "del", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "acronym", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "sub", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ins", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "select", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "script", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "var", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "tt", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "big", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "cite", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "b", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "textarea", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "button", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "em", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "object", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "bdo", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "img", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "map", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "code", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "a", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "label", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "input", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "strong", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "span", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "dfn", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "noscript", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "sup", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "samp", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "kbd", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "i", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "small", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "br", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> xhtmlInlNoRubyMix;

    /**
     * Gets the value of the xhtmlInlNoRubyMix property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xhtmlInlNoRubyMix property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXhtmlInlNoRubyMix().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link XhtmlAbbrType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlQType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlEditType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlAcronymType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlEditType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlSelectType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlScriptType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlVarType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlCiteType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlTextareaType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlButtonType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlEmType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlObjectType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlBdoType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlImgType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlMapType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlCodeType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlAType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlLabelType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInputType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlStrongType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlSpanType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlDfnType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlNoscriptType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlSampType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlKbdType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlBrType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getXhtmlInlNoRubyMix() {
        if (xhtmlInlNoRubyMix == null) {
            xhtmlInlNoRubyMix = new ArrayList<JAXBElement<?>>();
        }
        return this.xhtmlInlNoRubyMix;
    }

}
