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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for xhtml.object.type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xhtml.object.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{}xhtml.object.content"/>
 *       &lt;attGroup ref="{}xhtml.object.attlist"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xhtml.object.type", propOrder = {
    "content"
})
public class XhtmlObjectType implements XhtmlType{

    @XmlElementRefs({
        @XmlElementRef(name = "h1", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "em", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "param", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "h6", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "h2", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "var", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "code", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "dfn", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "del", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "label", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "bdo", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "select", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "samp", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "dl", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "object", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "big", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "b", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "table", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "h3", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "input", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "sup", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "fieldset", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "address", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "abbr", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ol", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "sub", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "img", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "map", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "a", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "i", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "h4", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "br", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "blockquote", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "script", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "small", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "p", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "hr", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "noscript", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "kbd", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "h5", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "acronym", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "div", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "span", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ruby", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "textarea", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "cite", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ins", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "pre", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "strong", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ul", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "form", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "q", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "tt", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "button", type = JAXBElement.class, required = false)
    })
    @XmlMixed
    protected List<Serializable> content;
    @XmlAttribute(name = "declare")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String declare;
    @XmlAttribute(name = "classid")
    protected String classid;
    @XmlAttribute(name = "codebase")
    protected String codebase;
    @XmlAttribute(name = "data")
    protected String data;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "codetype")
    protected String codetype;
    @XmlAttribute(name = "archive")
    protected List<String> archive;
    @XmlAttribute(name = "standby")
    protected String standby;
    @XmlAttribute(name = "height")
    protected String height;
    @XmlAttribute(name = "width")
    protected String width;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "tabindex")
    protected BigInteger tabindex;
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
    @XmlAttribute(name = "usemap")
    protected String usemap;

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
     * {@link JAXBElement }{@code <}{@link XhtmlH1Type }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlEmType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlParamType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlH6Type }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlH2Type }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlVarType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlCodeType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlDfnType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlEditType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlLabelType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlBdoType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlSelectType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlSampType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlDlType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlObjectType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlTableType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlH3Type }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInputType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlFieldsetType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlAddressType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlAbbrType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlOlType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlImgType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlMapType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlAType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlH4Type }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlBrType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlBlockquoteType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlScriptType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlPType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlHrType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlNoscriptType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlKbdType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlH5Type }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlAcronymType }{@code >}
     * {@link String }
     * {@link JAXBElement }{@code <}{@link XhtmlDivType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlSpanType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlRubyType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlTextareaType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlCiteType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlEditType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlPreType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlStrongType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlUlType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlFormType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlQType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlInlPresType }{@code >}
     * {@link JAXBElement }{@code <}{@link XhtmlButtonType }{@code >}
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
     * Gets the value of the declare property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeclare() {
        return declare;
    }

    /**
     * Sets the value of the declare property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeclare(String value) {
        this.declare = value;
    }

    /**
     * Gets the value of the classid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassid() {
        return classid;
    }

    /**
     * Sets the value of the classid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassid(String value) {
        this.classid = value;
    }

    /**
     * Gets the value of the codebase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodebase() {
        return codebase;
    }

    /**
     * Sets the value of the codebase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodebase(String value) {
        this.codebase = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData(String value) {
        this.data = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the codetype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodetype() {
        return codetype;
    }

    /**
     * Sets the value of the codetype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodetype(String value) {
        this.codetype = value;
    }

    /**
     * Gets the value of the archive property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the archive property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArchive().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getArchive() {
        if (archive == null) {
            archive = new ArrayList<String>();
        }
        return this.archive;
    }

    /**
     * Gets the value of the standby property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStandby() {
        return standby;
    }

    /**
     * Sets the value of the standby property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStandby(String value) {
        this.standby = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeight(String value) {
        this.height = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidth(String value) {
        this.width = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the tabindex property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTabindex() {
        return tabindex;
    }

    /**
     * Sets the value of the tabindex property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTabindex(BigInteger value) {
        this.tabindex = value;
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

    /**
     * Gets the value of the usemap property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsemap() {
        return usemap;
    }

    /**
     * Sets the value of the usemap property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsemap(String value) {
        this.usemap = value;
    }

}
