//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.09.06 at 06:40:12 PM PDT 
//


package org.cihi.claml.schema;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for xhtml.h5.type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xhtml.h5.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{}xhtml.h5.content"/>
 *       &lt;attGroup ref="{}xhtml.h5.attlist"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xhtml.h5.type", propOrder = {
    "content"
})
public class XhtmlH5Type implements XhtmlType{

    @XmlElementRefs({
        @XmlElementRef(name = "input", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "i", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "q", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "samp", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "noscript", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "button", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "object", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "label", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ruby", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "strong", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "tt", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "bdo", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "b", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "small", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "a", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "em", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "img", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ins", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "kbd", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "br", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "cite", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "span", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "select", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "dfn", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "var", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "acronym", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "abbr", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "map", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "script", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "sub", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "textarea", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "del", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "code", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "sup", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "big", type = JAXBElement.class, required = false)
    })
    @XmlMixed
    protected List<Serializable> content;
    @XmlAttribute(name = "space", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String space;
    @XmlAttribute(name = "class")
    protected String clazz;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "title")
    protected String title;
    @XmlAttribute(name = "style")
    protected String style;
    @XmlAttribute(name = "onclick")
    protected String onclick;
    @XmlAttribute(name = "ondblclick")
    protected String ondblclick;
    @XmlAttribute(name = "onmousedown")
    protected String onmousedown;
    @XmlAttribute(name = "onmouseup")
    protected String onmouseup;
    @XmlAttribute(name = "onmouseover")
    protected String onmouseover;
    @XmlAttribute(name = "onmousemove")
    protected String onmousemove;
    @XmlAttribute(name = "onmouseout")
    protected String onmouseout;
    @XmlAttribute(name = "onkeypress")
    protected String onkeypress;
    @XmlAttribute(name = "onkeydown")
    protected String onkeydown;
    @XmlAttribute(name = "onkeyup")
    protected String onkeyup;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;
    @XmlAttribute(name = "lang")
    protected String nameAttribute;
    @XmlAttribute(name = "dir")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String dir;

    /**
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link XhtmlInputType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlQType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlSampType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlNoscriptType }{@code >}
     * {@link String }
     * {@link JAXBElement }{@code <}{@link XhtmlButtonType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlObjectType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlLabelType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlRubyType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlStrongType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlBdoType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlAType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlEmType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlImgType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlEditType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlKbdType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlBrType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlCiteType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlSpanType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlSelectType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlDfnType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlVarType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlAcronymType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlAbbrType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlMapType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlScriptType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlTextareaType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlEditType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlCodeType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * 
     * 
     */
    public List<Serializable> getContent() {
        if (content == null) {
            content = new ArrayList<Serializable>();
        }
        return this.content;
    }

    /**
     * Gets the value of the space property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpace() {
        if (space == null) {
            return "preserve";
        } else {
            return space;
        }
    }

    /**
     * Sets the value of the space property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpace(String value) {
        this.space = value;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStyle(String value) {
        this.style = value;
    }

    /**
     * Gets the value of the onclick property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnclick() {
        return onclick;
    }

    /**
     * Sets the value of the onclick property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnclick(String value) {
        this.onclick = value;
    }

    /**
     * Gets the value of the ondblclick property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOndblclick() {
        return ondblclick;
    }

    /**
     * Sets the value of the ondblclick property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOndblclick(String value) {
        this.ondblclick = value;
    }

    /**
     * Gets the value of the onmousedown property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnmousedown() {
        return onmousedown;
    }

    /**
     * Sets the value of the onmousedown property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnmousedown(String value) {
        this.onmousedown = value;
    }

    /**
     * Gets the value of the onmouseup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnmouseup() {
        return onmouseup;
    }

    /**
     * Sets the value of the onmouseup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnmouseup(String value) {
        this.onmouseup = value;
    }

    /**
     * Gets the value of the onmouseover property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnmouseover() {
        return onmouseover;
    }

    /**
     * Sets the value of the onmouseover property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnmouseover(String value) {
        this.onmouseover = value;
    }

    /**
     * Gets the value of the onmousemove property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnmousemove() {
        return onmousemove;
    }

    /**
     * Sets the value of the onmousemove property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnmousemove(String value) {
        this.onmousemove = value;
    }

    /**
     * Gets the value of the onmouseout property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnmouseout() {
        return onmouseout;
    }

    /**
     * Sets the value of the onmouseout property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnmouseout(String value) {
        this.onmouseout = value;
    }

    /**
     * Gets the value of the onkeypress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnkeypress() {
        return onkeypress;
    }

    /**
     * Sets the value of the onkeypress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnkeypress(String value) {
        this.onkeypress = value;
    }

    /**
     * Gets the value of the onkeydown property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnkeydown() {
        return onkeydown;
    }

    /**
     * Sets the value of the onkeydown property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnkeydown(String value) {
        this.onkeydown = value;
    }

    /**
     * Gets the value of the onkeyup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnkeyup() {
        return onkeyup;
    }

    /**
     * Sets the value of the onkeyup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnkeyup(String value) {
        this.onkeyup = value;
    }

    /**
     * Gets the value of the lang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
    }

    /**
     * Gets the value of the nameAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameAttribute() {
        return nameAttribute;
    }

    /**
     * Sets the value of the nameAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameAttribute(String value) {
        this.nameAttribute = value;
    }

    /**
     * Gets the value of the dir property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDir() {
        return dir;
    }

    /**
     * Sets the value of the dir property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDir(String value) {
        this.dir = value;
    }

}
