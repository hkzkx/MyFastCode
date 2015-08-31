package com.code.supports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;


//import org.apache.catalina.core.ApplicadtionParddt; 
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.MultiPartInputStream;
import org.eclipse.jetty.util.MultiPartInputStream.MultiPart;
import org.springframework.security.core.context.SecurityContextImpl;

import com.code.security.impl.MMBUserDetails;

public class Servlet3UploadServlet extends HttpServlet {

	private static final long	serialVersionUID	= -8149820677143188132L;

	private static final Logger	log					= Logger.getLogger(Servlet3UploadServlet.class);

	private static final String	char_encoding		= "utf8";

	private static final String	configFileKey		= "configFile";

	private static String		path;

	private List<String>		allowFileTypeList;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		ResourceBundle resb = ResourceBundle.getBundle(config.getInitParameter(configFileKey));
		log.info("config file : " + resb.getBaseBundleName());
		Enumeration<String> keys = resb.getKeys();
		if (keys != null) {
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String v = resb.getString(key);
				log.info("key = v:[" + key + " = " + v + "]");
			}
		}

		String rootPath = resb.getString("upload.path.absolute");
		String[] allowFileTypes = resb.getString("allow.file.types").split(",");
		allowFileTypeList = Arrays.asList(allowFileTypes);
		// path = config.getServletContext().getRealPath(rootPath);
		path = rootPath;
		log.info("文件保存位置: " + path);
		File dir = new File(path);
		if (!dir.exists()) {
			try {
				makeDir(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.init(config);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8;");

		UploadMessage uploadMessage = new UploadMessage();
		SecurityContextImpl context = getSecurity(req);
		if (context == null) {
			uploadMessage.setStatus(1);
			uploadMessage.setMessage("请登录");
			String json = JsonUtil.java2json(uploadMessage);
			resp.getOutputStream().write(json.getBytes(char_encoding));
			return;
		}

		List<UploadFileInfo> msgList = new ArrayList<UploadFileInfo>();
		uploadMessage.setUploadFiles(msgList);
		uploadMessage.setStatus(0); // 0成功
		try {
			List<Part> list = getParts(req);
//			MultiPartInputStream mpis = (MultiPartInputStream) req.getAttribute("org.eclipse.jetty.servlet.MultiPartFile.multiPartInputStream");
			if (list.isEmpty()) {
				uploadMessage.setStatus(1);
				uploadMessage.setMessage("请选择上传文件");
				String json = JsonUtil.java2json(uploadMessage);
				resp.getOutputStream().write(json.getBytes(char_encoding));
				return;
			}
//			Collection<Part> parts = mpis.getParts();
			int count = 0;
			for (Part part : list) {
				MultiPart mpart = (MultiPart) part;
				String fileName = mpart.getContentDispositionFilename();

				if (StringUtils.isEmpty(fileName)) {
					continue;
				}
				UploadFileInfo msg = new UploadFileInfo();
				String contentType = part.getContentType();
				log.info("开始接收文件[" + contentType + "]：" + fileName);
				if (contentType == null || !allowFileTypeList.contains(contentType)) {
					log.warn("mime type[" + contentType + "] unsurported ");
					uploadMessage.setStatus(1);
					msg.setMsg("文件类型不支持");
					msg.setField(part.getName());
					msg.setOrgName(fileName);
					msgList.add(msg);
					continue;
				}

				MMBUserDetails details = (MMBUserDetails) context.getAuthentication().getDetails();

				String userDir = getDir(details.getUserID().toString(), details.getUserType().toString()) + "/";
				String filePath = path + "/" + userDir;
				File dir = new File(filePath);
				if (!dir.exists()) {
					makeDir(dir);
				}

				String modifiedName = System.currentTimeMillis() + "_" + part.hashCode() + fileName.substring(fileName.indexOf("."));
				String fileQualifiedPath = filePath + modifiedName;
				log.info(fileName + " : - " + fileQualifiedPath);
				mpart.getFile().renameTo(new File(fileQualifiedPath));
				++count;

				msg.setPath(userDir + modifiedName);
				msg.setOrgName(fileName);
				msg.setField(part.getName());
				msgList.add(msg);
				log.info(count + "文件接收完毕");
				mpart.delete();//删除临时文件
			}
			String json = JsonUtil.java2json(uploadMessage);
			resp.getOutputStream().write(json.getBytes(char_encoding));

			log.info("done");
		} catch (Exception e) {
			e.printStackTrace();
			uploadMessage.setStatus(1);
			String message = e.getMessage(); // ExceptionUtils.getFullStackTrace(e);
			uploadMessage.setMessage(message);
			String json = JsonUtil.java2json(uploadMessage);
			resp.getOutputStream().write(json.getBytes(char_encoding));
		}

	}

	private String getDir(String userId, String userType) throws IOException {
		return UserDirectory.calc8LByStr(userId + userType);
	}

	private void makeDir(File dir) throws IOException {
		if (!dir.exists()) {
			boolean created = dir.mkdirs();
			if (created) {
				File index = new File(dir + "/index.html");
				if (!index.exists()) {
					created = index.createNewFile();
					if (created) {
						FileWriter fw = new FileWriter(index);
						fw.write("<html></html>");
						fw.close();
					}
				}
			}
		}
	}

	private SecurityContextImpl getSecurity(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null)
			return null;
		Object security = session.getAttribute("SPRING_SECURITY_CONTEXT");
		if (security == null)
			return null;

		return (SecurityContextImpl) security;
	}
	
	private List<Part> getParts(HttpServletRequest req) throws IOException, ServletException{
		MultiPartInputStream mpis = (MultiPartInputStream) req.getAttribute("org.eclipse.jetty.servlet.MultiPartFile.multiPartInputStream");
		Collection<Part> parts = mpis.getParts();
		List<Part> list = new ArrayList<Part>();
		for (Part part : parts) {
			MultiPart mpart = (MultiPart) part;
			String fileName = mpart.getContentDispositionFilename();

			if (StringUtils.isEmpty(fileName)) {
				continue;
			}
			list.add(part);
		}
		
		return list;
	}
}
