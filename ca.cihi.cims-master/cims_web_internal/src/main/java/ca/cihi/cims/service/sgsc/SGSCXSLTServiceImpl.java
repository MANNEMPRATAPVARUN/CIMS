package ca.cihi.cims.service.sgsc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import ca.cihi.cims.data.mapper.SGSCMapper;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.ViewService;

@Service
public class SGSCXSLTServiceImpl implements SGSCService, ApplicationContextAware {

	@Autowired
	protected ClassificationService service;

	private String singleTdRegex = ".*<tr><td><a[^<]*</a></td></tr></table>";
	private Pattern singleTdpattern = Pattern.compile(singleTdRegex);
	private static Pattern ccdInterventionLinkPattern = Pattern.compile("<a name=[^>]+>");

	private static final Log LOGGER = LogFactory.getLog(SGSCXSLTServiceImpl.class);
	private ApplicationContext context;

	public String getContent(SupplementContentRequest request) {
		SupplementContentGeneratorFactory factory = context.getBean(SupplementContentGeneratorFactory.class);

		SGSCMapper mapper = context.getBean(SGSCMapper.class);
		ConceptService conceptService = context.getBean(ConceptService.class);
		ClassificationService classificationService = context.getBean(ClassificationService.class);
		ViewService viewService = context.getBean(ViewService.class);

		SupplementContentGenerator generator = factory.createGenerator(request);
		generator.setSgscMapper(mapper);
		generator.setConceptService(conceptService);
		generator.setClassificationService(classificationService);
		generator.setViewService(viewService);

		return generator.generateSupplementContent(request);
	}

	public String replaceSystemGeneratedSupplementContent(String xhtmlString, Long currentContextId,
			Long priorContextId, Boolean folio) {
		//"<section><label> Section 2 Rubric Finder</label></section><report src="CCIRubricFinder" lang="eng" section="2"></report>"

		if (xhtmlString.contains("<report")) {
			int start = xhtmlString.indexOf("<report");
			String endTag = "/>";
			boolean isXhtml = true;
			int end = xhtmlString.indexOf(endTag, start);
			if (end <= 0 || end > start + 50) {
				endTag = "</report>";
				end = xhtmlString.indexOf("</report>", start);
				isXhtml = false;
				if (end <= 0) {
					endTag = "/>";
					end = xhtmlString.indexOf(endTag, start);
					isXhtml = true;
				}
			}
			String xmlString = xhtmlString.substring(start, end + endTag.length());
			try {
				SAXBuilder builder = new SAXBuilder();
				Document document = builder.build(new ByteArrayInputStream(xmlString.getBytes()));
				Element report = (Element) XPath.selectSingleNode(document, "/report");
				String src = report.getAttributeValue("src");
				String language = report.getAttributeValue("lang").toUpperCase();
				Long section = null;
				try {
					if (report.getAttributeValue("section") != null)
						section = Long.valueOf(Long.parseLong(report.getAttributeValue("section")));
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("Error parsing section attribute: " + report.getAttributeValue("section"));
				}
				String type = report.getAttributeValue("type");
				Integer qualifier = null;
				try {
					if (report.getAttributeValue("qualifier") != null)
						qualifier = Integer.valueOf(Integer.parseInt(report.getAttributeValue("qualifier")));
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("Error parsing qualifier attribute: " + report.getAttributeValue("qualifier"));
				}
				String reportContent = getContent(new SupplementContentRequest(src, language, section, type, qualifier,
						currentContextId.longValue(), priorContextId.longValue(), folio));
				if (isXhtml) {
					xhtmlString = xhtmlString.replace(xmlString, reportContent);
				} else {
					String tableStartTag = "<table";
					String tableEndTag = "</table>";
					String newReportContent = reportContent;
					String reportBody = reportContent;
					if (reportContent.contains(tableStartTag)){
						//get only table part
						int ts = reportContent.indexOf(tableStartTag);
						int te = reportContent.lastIndexOf(tableEndTag);
						if (te >0){
							newReportContent = reportContent.substring(0, te + tableEndTag.length() );
						}	
						newReportContent = newReportContent.substring(ts);

						//merge tables
						newReportContent = newReportContent.replace("</thead></table></div><table style='width:auto;'><tr>", "</thead><tr>");
						newReportContent = newReportContent.replace("</thead></table></div><table style='width:auto'><tr>", "</thead><tr>");
						//correct possible mismatch
						newReportContent = newReportContent.replace("</td></table>", "</td></tr></table>");
					}

					newReportContent = newReportContent.replace("</td></tr><td", "</td></tr><tr><td");
					newReportContent = newReportContent.replace("</td><tr><td", "</td></tr><tr><td");

					//escape chars
					newReportContent = newReportContent.replace(" & ", " &amp; ");
					newReportContent = Pattern.compile("(\\w)&(\\w)").matcher(newReportContent).replaceAll("$1&amp;$2");

					newReportContent = newReportContent.replaceAll(" > ", " &gt; ");
					newReportContent = newReportContent.replaceAll(" < ", " &lt; ");
					newReportContent = Pattern.compile(" >(\\d)").matcher(newReportContent).replaceAll(" &gt;$1");
					newReportContent = Pattern.compile(" <(\\d)").matcher(newReportContent).replaceAll(" &lt;$1");

					newReportContent = Pattern.compile("< (\\d)").matcher(newReportContent).replaceAll(" &lt; $1");
					newReportContent = Pattern.compile("> (\\d)").matcher(newReportContent).replaceAll(" &gt; $1");

					newReportContent = newReportContent.replaceAll("&nbsp;", "&#160;");
					reportBody = newReportContent;

					//special handling for CCI appendix B Rubric Finder first 3 Sections html correction
					if (src.equals("CCIRubricFinder") || src.equals("CCIGroup") ){
						Map<String, String> reportDetails = new HashMap<>();
						if (src.equals("CCIRubricFinder")){
							List<Long> targets1 = Arrays.asList(new Long(1), new Long(2));
							List<Long> targets2 = Arrays.asList(new Long(5), new Long(6) , new Long(7) , new Long(8));
							if (targets1.contains(section)){
								Matcher matcher = singleTdpattern.matcher(reportContent);
								if (matcher.lookingAt()){
									newReportContent = newReportContent.replace("</tr></table>", "<td></td></tr></table>");
								}
							}
							if (targets2.contains(section)){
								newReportContent = newReportContent.replace("<td colspan='4'><span class='title'>", "<td>");
								newReportContent = newReportContent.replace("</span></td></tr></table><div id='sticker'><table", "</td></tr></table><table");
								newReportContent = newReportContent.replace("</table></div><div><table", "</table><table");
							}
	
							Pattern p = Pattern.compile("javascript:getRubricContent\\(([^>]+)\\);\\\">([^<]+)</a>");
							Matcher m = p.matcher(newReportContent);
							while (m.find()){
								String argString = m.group(1).replaceAll("'","");
								String itemName = m.group(2);
								String[] params = argString.split(",");
								language = params[0];
								Long contextId = Long.parseLong(params[1]);
								String sectionCode = params[2];
								String groupCode = params[3];
								Long id  = Long.parseLong(params[4]);
								String itemDetail = service.getCCIRubricContent(language, contextId, sectionCode, groupCode, id);
								itemDetail =  itemDetail.replaceAll("<table class='conceptTable'><tr><td colspan='4'>", "<table class='conceptTable'><tr><td>");
								itemDetail =  itemDetail.replaceAll("<div id='sticker'>", "");
								itemDetail =  itemDetail.replaceAll("<div>", "");
								itemDetail =  itemDetail.replaceAll("</div>", "");
	
								itemDetail =  itemDetail.replaceAll("<span class='title'>", "");
								itemDetail =  itemDetail.replaceAll("<span>", "");
								itemDetail =  itemDetail.replaceAll("</span>", "");
								reportDetails.put(itemName, itemDetail);
							}
						}else if (src.equals("CCIGroup")){
							//javascript:getGroupContent('ENG',5251621,1,'A',1129604);">
							Pattern p = Pattern.compile("javascript:getGroupContent\\(([^>]+)\\);\\\">([^<]+)</a>");
							Matcher m = p.matcher(newReportContent);
							while (m.find()){
								String argString = m.group(1).replaceAll("'","");
								String itemName = m.group(2);
								String[] params = argString.split(",");
								language = params[0];
								Long contextId = Long.parseLong(params[1]);
								String sectionCode = params[2];
								String groupCode = params[3];
								Long id  = Long.parseLong(params[4]);
								String itemDetail = service.getCCIGroupContent(language, contextId, sectionCode, groupCode, id);
								itemDetail =  itemDetail.replace("<table class='conceptTable'>", "<table class='conceptTable'><tr><td></td><td></td><td></td><td></td></tr>");
								itemDetail =  itemDetail.replaceAll("<td/>", "<td></td>");
								itemDetail =  itemDetail.replaceAll("<tr><td class=\"chp\" colspan=\"3\"><a name=\"\\d+\"/></td><td></td></tr>", "");
								itemDetail =  itemDetail.replaceAll("<tr><td colspan=\"4\" height=\"\\dpx\"/></tr>", "");
								itemDetail =  itemDetail.replaceAll("<ul style=\"margin-top: 0; margin-bottom: 0;\"/>", "");
								itemDetail =  itemDetail.replaceAll("height='10'", "");
								reportDetails.put(itemName, itemDetail);
							}
						}

						String detailsXML = "<reportdetails>";
						for(Entry<String, String> entry: reportDetails.entrySet()) {
							detailsXML +="<reportdetail name =\"" + entry.getKey() + "\">" + entry.getValue() + "</reportdetail>";
						}
						detailsXML += "</reportdetails>";
						reportBody = "<main><![CDATA[" + newReportContent + "]]></main>";
						if (!reportDetails.isEmpty()){
							reportBody +="<detail><![CDATA[" + detailsXML + "]]></detail>";
						}

					}else if (src.equals("CCIIntervention") || (src.equals("CCIGenAttrb"))){
						if (newReportContent.startsWith("<tr><td colspan='4'><span class='title'>")){
							newReportContent = newReportContent.replaceAll("<span class='title'>", "");
							newReportContent = newReportContent.replaceAll("<td/>", "<td></td>");
							newReportContent = newReportContent.replaceAll("<td colspan=\"4\" height=\"3px\"/>", "<td colspan=\"4\"></td>");
							newReportContent = newReportContent.replaceAll("<td height='10'", "<td ");

							newReportContent = ccdInterventionLinkPattern.matcher(newReportContent).replaceAll("");
							newReportContent = "<table>" +  newReportContent + "<tr><td></td><td></td><td></td><td></td></tr></table>";
						}
						reportBody = "<main><![CDATA[" + newReportContent + "]]></main>";

					}else if (src.equals("CCIDisabledRefValue")){
						String mark = "</div><table style='width:auto;'></table>";
						if (newReportContent.endsWith(mark)){
							newReportContent = newReportContent.substring(0, newReportContent.length() - mark.length());
						}
						reportBody = "<main><![CDATA[" + newReportContent + "]]></main>";
					}

					String replacement = "<report>" + reportBody + endTag;
					xhtmlString = xhtmlString.replace(xmlString, replacement);
				}
			} catch (JDOMException e) {
				LOGGER.error(e);
				return xhtmlString.replace(xmlString, "");
			} catch (IOException e) {
				LOGGER.error(e);
				return xhtmlString.replace(xmlString, "");
			}
		}
		return xhtmlString;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

}
