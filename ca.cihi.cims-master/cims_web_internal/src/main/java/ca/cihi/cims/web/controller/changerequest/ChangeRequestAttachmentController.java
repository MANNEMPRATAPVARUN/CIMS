package ca.cihi.cims.web.controller.changerequest;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.cihi.cims.model.changerequest.AttachmentFormat;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.DocumentReference;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.FileService;
import ca.cihi.cims.service.PublicationService;

@Controller
public class ChangeRequestAttachmentController {
	private static final Log LOGGER = LogFactory.getLog(ChangeRequestAttachmentController.class);

	private static final int BUFSIZE = 4096;
	private static final String CONTENT_TYPE_PDF = "application/pdf";
	private static final String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";
	private static final String CONTENT_TYPE_TXT = "text/plain";
	private static final String CONTENT_TYPE_HTML = "text/html";
	private static final String CONTENT_TYPE_ZIP = "application/zip";
	// private static final String CONTENT_TYPE_HTML_ISO8859 = "text/html; charset=ISO-8859-1";
	// private static final String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";

	@Autowired
	private ChangeRequestService changeRequestService;

	@Autowired
	private FileService fileService;

	@Autowired
	private PublicationService publicationService;

	private String getContentType(final String type) {
		if (AttachmentFormat.PDF_FILE.getCode().equals(type)) {
			return CONTENT_TYPE_PDF;
		} else if (AttachmentFormat.EXCEL_FILE.getCode().equals(type)) {
			return CONTENT_TYPE_EXCEL;
		} else if (AttachmentFormat.TXT_FILE.getCode().equals(type)) {
			return CONTENT_TYPE_TXT;
		} else if (AttachmentFormat.ZIP.getCode().equalsIgnoreCase(type)) {
			return CONTENT_TYPE_ZIP;
		}
		return CONTENT_TYPE_HTML;
	}

	@RequestMapping("/openChangeRequestFile.htm")
	public String openChangeRequestFile(@RequestParam("changeRequestId") Long changeRequestId,
			@RequestParam("attachmentId") Long attachmentId, @RequestParam("attachmentType") String attachmentType,
			final HttpServletRequest request, final HttpServletResponse response) {
		LOGGER.debug("  attachmentId :" + attachmentId);
		String fileName = null;
		ChangeRequestDTO changeRequestDTO = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequestId);
		if ("urc".equalsIgnoreCase(attachmentType)) {
			for (DocumentReference urcAttchment : changeRequestDTO.getUrcAttachments()) {
				if (urcAttchment.getDocumentReferenceId().longValue() == attachmentId.longValue()) {
					fileName = urcAttchment.getFileName();
					break;
				}
			}
		} else {
			for (DocumentReference otherAttchment : changeRequestDTO.getOtherAttachments()) {
				if (otherAttchment.getDocumentReferenceId().longValue() == attachmentId.longValue()) {
					fileName = otherAttchment.getFileName();
					break;
				}
			}
		}

		DataInputStream inputStream = null;
		ServletOutputStream stream = null;
		int length = 0;

		String fileExtension = null;
		if (fileName.lastIndexOf(".") > 0) {
			fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		try {
			stream = response.getOutputStream();
			StringBuilder sb_fileName = new StringBuilder(String.valueOf(changeRequestId));
			sb_fileName.append("_" + attachmentType + "_");
			sb_fileName.append(fileName);

			File file = fileService.getFile(sb_fileName.toString());
			byte[] bbuf = new byte[BUFSIZE];
			inputStream = new DataInputStream(new FileInputStream(file));
			// set response headers
			setHeaders(response, file, sb_fileName.toString(), fileExtension);
			while ((inputStream != null) && ((length = inputStream.read(bbuf)) != -1)) {
				stream.write(bbuf, 0, length);
			}
		} catch (IOException e) {
			new RuntimeException(e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (stream != null) {
					stream.close();
					stream.flush();
				}
			} catch (IOException ioe) {
				LOGGER.error(ioe.getCause());
			}
		}
		return null;
	}

	@RequestMapping("/downloadReleaseZipFile.htm")
	public String openReleaseZipFile(@RequestParam("releaseId") Long releaseId, final HttpServletRequest request,
			final HttpServletResponse response) {
		LOGGER.debug("  releaseId :" + releaseId);

		String releaseZipFileName = publicationService.findReleaseZipFileName(releaseId);

		DataInputStream inputStream = null;
		ServletOutputStream stream = null;
		int length = 0;
		String fileExtension = null;
		if (releaseZipFileName.lastIndexOf(".") > 0) {
			fileExtension = releaseZipFileName.substring(releaseZipFileName.lastIndexOf(".") + 1);
		}
		try {
			stream = response.getOutputStream();
			File file = fileService.getReleaseZipFile(releaseZipFileName);
			byte[] bbuf = new byte[BUFSIZE];
			inputStream = new DataInputStream(new FileInputStream(file));
			setHeaders(response, file, releaseZipFileName, fileExtension);
			while ((inputStream != null) && ((length = inputStream.read(bbuf)) != -1)) {
				stream.write(bbuf, 0, length);
			}
		} catch (IOException e) {
			new RuntimeException(e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (stream != null) {
					stream.close();
					stream.flush();
				}
			} catch (IOException ioe) {
				LOGGER.error(ioe.getCause());
			}
		}

		return null;
	}

	@RequestMapping("/downloadSnapShotZipFile.htm")
	public String openSnapShotZipFile(@RequestParam("snapShotId") Long snapShotId, final HttpServletRequest request,
			final HttpServletResponse response) {
		LOGGER.debug("  snapShotId :" + snapShotId);

		String snapShotZipFileName = publicationService.findSnapShotZipFileName(snapShotId);

		DataInputStream inputStream = null;
		ServletOutputStream stream = null;
		int length = 0;
		String fileExtension = null;
		if (snapShotZipFileName.lastIndexOf(".") > 0) {
			fileExtension = snapShotZipFileName.substring(snapShotZipFileName.lastIndexOf(".") + 1);
		}
		try {
			stream = response.getOutputStream();
			File file = fileService.getSnapshotZipFile(snapShotZipFileName);
			byte[] bbuf = new byte[BUFSIZE];
			inputStream = new DataInputStream(new FileInputStream(file));
			setHeaders(response, file, snapShotZipFileName, fileExtension);
			while ((inputStream != null) && ((length = inputStream.read(bbuf)) != -1)) {
				stream.write(bbuf, 0, length);
			}
		} catch (IOException e) {
			new RuntimeException(e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (stream != null) {
					stream.close();
					stream.flush();
				}
			} catch (IOException ioe) {
				LOGGER.error(ioe.getCause());
			}
		}

		return null;
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	private void setHeaders(final HttpServletResponse response, final File file, final String fileName,
			final String type) {
		response.setContentLength((int) file.length());
		response.addHeader("Content-Disposition", "attachment; filename=" + fileName);

		response.setContentType(getContentType(type));
	}

	public void setPublicationService(PublicationService publicationService) {
		this.publicationService = publicationService;
	}

}
