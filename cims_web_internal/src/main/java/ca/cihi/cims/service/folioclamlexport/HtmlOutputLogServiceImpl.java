package ca.cihi.cims.service.folioclamlexport;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.cihi.cims.data.mapper.AdminMapper;
import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;

@Service
public class HtmlOutputLogServiceImpl implements HtmlOutputLogService {
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private Map<Long, List<String>> detailedLogs = new ConcurrentHashMap<>();

	@Autowired
	private AdminMapper adminMapper;

	public AdminMapper getAdminMapper() {
		return adminMapper;
	}

	public void setAdminMapper(AdminMapper adminMapper) {
		this.adminMapper = adminMapper;
	}

	@Override
	public List<HtmlOutputLog> getHtmlOutputLogs() {
		return adminMapper.getHtmlOutputLogs();
	}

	@Override
	public void insertHtmlOutputLog(HtmlOutputLog log) {
		adminMapper.insertHtmlOutputLog(log);
	}

	@Override
	public void updateStatus(Long htmlOutputLogId, String status) {
		this.updateStatus(htmlOutputLogId, status, null);
	}

	@Override
	public void updateStatus(Long htmlOutputLogId, String status, String zipFileName) {
		HtmlOutputLog log = new HtmlOutputLog();
		log.setHtmlOutputLogId(htmlOutputLogId);
		log.setStatusCode(status);
		log.setZipFileName(zipFileName);
		adminMapper.updateHtmlOutputLog(log);
	}

	@Override
	public List<String> getDetailedLog(Long htmlOutputLogId) {
		return this.detailedLogs.get(htmlOutputLogId);
	}

	@Override
	public void addDetailLog(Long htmlOutputLogId, String msg) {
		List<String> detailedLog = this.detailedLogs.get(htmlOutputLogId);
		detailedLog.add(LocalDateTime.now().format(formatter) + " " + msg);
	}

	@Override
	public void initDetailedLog(Long htmlOutputLogId) {
		this.detailedLogs = new ConcurrentHashMap<>();
		detailedLogs.put(htmlOutputLogId, Collections.synchronizedList(new ArrayList<String>()));
	}

}
