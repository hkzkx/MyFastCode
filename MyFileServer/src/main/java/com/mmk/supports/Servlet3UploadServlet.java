package com.mmk.supports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
//import org.apache.catalina.core.ApplicationPart;

import org.apache.commons.lang.StringUtils;

@WebServlet(urlPatterns = { "/upload" }, 
	loadOnStartup = 0, 
	initParams = { @WebInitParam(name = "configFile", value = "/config/common") })
@MultipartConfig(fileSizeThreshold = 10)
public class Servlet3UploadServlet extends HttpServlet {

	private static final long serialVersionUID = -8149820677143188132L;

	private static final String configFileKey = "configFile";
	private static String rootPath;

	private List<String> allowFileTypeList;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		ResourceBundle resb = ResourceBundle.getBundle(config.getInitParameter(configFileKey));
		rootPath = resb.getString("upload.path.absolute");
		System.out.println("rootPath is " + rootPath);
		String[] allowFileTypes = resb.getString("allow.file.types").split(",");
		allowFileTypeList = Arrays.asList(allowFileTypes);
		File dir = new File(rootPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		super.init(config);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		List<UploadFileInfo> msgList = new ArrayList<UploadFileInfo>();
		UploadMessage uploadMessage = new UploadMessage();
		uploadMessage.setUploadFiles(msgList);
		uploadMessage.setStatus(0); // 0成功
		try {
			String userId = req.getParameter("userId");
			String userType = req.getParameter("userType");

			if (StringUtils.isBlank(userId) || StringUtils.isBlank(userType)) {

				uploadMessage.setStatus(1);
				uploadMessage.setMessage("both userId and userType can not be null");
				String json = JsonUtil.java2json(uploadMessage);
				resp.getWriter().write(json);
				return;
			}

			Collection<Part> parts = req.getParts();
			if (parts != null && parts.size() > 0) {
				for (Part parttemp : parts) {
					Part part = parttemp;
					String fileName = part.getSubmittedFileName();
					if (StringUtils.isEmpty(fileName)) {
						continue;
					}
					UploadFileInfo msg = new UploadFileInfo();
					String contentType = part.getContentType();

					if (contentType == null || !allowFileTypeList.contains(contentType)) {
						uploadMessage.setStatus(1);
						msg.setMsg("mime type[" + contentType + "] unsurported ");
						msgList.add(msg);
						msg.setField(part.getName());
						msg.setOrgName(fileName);
						continue;
					}

					String userDir = getDir(userId, userType) + "/";
					String filePath = rootPath + userDir;
					File dir = new File(filePath);
					if (!dir.exists()) {
						dir.mkdirs();
					}

					String modifiedName = System.currentTimeMillis()+"_" +part.hashCode() + fileName.substring(fileName.indexOf("."));
					String fileQualifiedPath = filePath + modifiedName;
					part.write(fileQualifiedPath);

					msg.setPath(userDir + modifiedName);
					msg.setOrgName(fileName);
					msg.setField(part.getName());
					msgList.add(msg);
				}
				String json = JsonUtil.java2json(uploadMessage);
				resp.getWriter().write(json);
				System.out.println("已经上传完毕");
			}
		} catch (Exception e) {
			e.printStackTrace();
			uploadMessage.setStatus(1);
			String message = e.getMessage(); // ExceptionUtils.getFullStackTrace(e);
			uploadMessage.setMessage(message);
			String json = JsonUtil.java2json(uploadMessage);
			resp.getWriter().write(json);
		}

	}

	private String getDir(String userId, String userType) {
		return UserDirectory.calc8LByStr(userId + userType);
	}

	private UploadMessage paramCheck(HttpServletRequest req, UploadMessage uploadMessage) {
		String userId = req.getParameter("userId");
		String userType = req.getParameter("userType");
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(userType)) {
			uploadMessage.setStatus(1);
			uploadMessage.setMessage("both userId and userType can not be null");
		}
		return uploadMessage;
	}

}
