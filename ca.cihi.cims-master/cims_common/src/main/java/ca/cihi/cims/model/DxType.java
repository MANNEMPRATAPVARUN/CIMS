package ca.cihi.cims.model;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.util.XmlUtils;

@XmlRootElement(name = "dxtype")
@XmlAccessorType(XmlAccessType.NONE)
public class DxType {

	@XmlRootElement(name = "dxtypes")
	@XmlAccessorType(XmlAccessType.NONE)
	public static class DxTypes {
		@XmlElement(name = "dxtype")
		private List<DxType> types;
	}

	public static List<DxType> types() {
		try {
			InputStream input = DxType.class.getResourceAsStream("/cihi_dxtypes.xml");
			String xml = new String(input.readAllBytes());
			return XmlUtils.deserialize(DxTypes.class, xml).types;
		} catch (Exception ex) {
			throw new CIMSException("Error loading DxTypes" + ex.getMessage(), ex);
		}
	}

	// ----------------------------------------------

	@XmlAttribute(name = "id", required = true)
	private long id;

	@XmlAttribute(name = "main", required = true)
	private String main;

	@XmlAttribute(name = "t1", required = true)
	private String t1;

	@XmlAttribute(name = "t2", required = true)
	private String t2;

	@XmlAttribute(name = "t3", required = true)
	private String t3;

	@XmlAttribute(name = "t4", required = true)
	private String t4;

	@XmlAttribute(name = "t6", required = true)
	private String t6;

	@XmlAttribute(name = "t9", required = true)
	private String t9;

	@XmlAttribute(name = "w", required = true)
	private String w;

	@XmlAttribute(name = "x", required = true)
	private String x;

	@XmlAttribute(name = "y", required = true)
	private String y;

	// ----------------------------------------------

	public long getId() {
		return id;
	}

	public String getMain() {
		return main;
	}

	public String getT1() {
		return t1;
	}

	public String getT2() {
		return t2;
	}

	public String getT3() {
		return t3;
	}

	public String getT4() {
		return t4;
	}

	public String getT6() {
		return t6;
	}

	public String getT9() {
		return t9;
	}

	public String getW() {
		return w;
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public void setT1(String t1) {
		this.t1 = t1;
	}

	public void setT2(String t2) {
		this.t2 = t2;
	}

	public void setT3(String t3) {
		this.t3 = t3;
	}

	public void setT4(String t4) {
		this.t4 = t4;
	}

	public void setT6(String t6) {
		this.t6 = t6;
	}

	public void setT9(String t9) {
		this.t9 = t9;
	}

	public void setW(String w) {
		this.w = w;
	}

	public void setX(String x) {
		this.x = x;
	}

	public void setY(String y) {
		this.y = y;
	}

}
