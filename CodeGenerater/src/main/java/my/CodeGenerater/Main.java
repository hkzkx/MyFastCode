package my.CodeGenerater;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import my.db.DBKey;
import my.db.DBUtil;

import org.springframework.util.StringUtils;

import com.my.bo.Controller;
import com.my.bo.Dao;
import com.my.bo.Dto;
import com.my.bo.Field;
import com.my.bo.Form;
import com.my.bo.IService;
import com.my.bo.Mapper;
import com.my.bo.Messages;
import com.my.bo.Service;

import freemarker.template.TemplateException;

/**
 * 基于Mysql建模，生成基础代码，包括： 单表数据操作都已经在BaseService中表现
 * 
 * 
 * Mybatis mapper xml DTO DAO 业务接口 业务接口实现 客户端Spring Controller
 * 
 * @author Administrator
 *
 */
public class Main {
	private static String delimiters = ", ";
	private static final String projectsPath = "Q:/guangqun/trunk/";
	private static final String package_ = "com.mmb.{layer}.{module}"; // 根据命名约定，不可变
	private static final String remotePath = projectsPath+"remote/src/main/java/"; // 业务存根代码生成的路径
	private static final String soaPath = projectsPath+"soa/src/main/java/"; // 远程服务代码生成的路径
	private static final String webPath = projectsPath+"web/src/main/java/"; // 应用代码生成的路径
	private static final String ftlPath = projectsPath+"web/src/main/webapp/WEB-INF/ftl/";//生成的 ftl from 存放位置 
	private static final String messagesPath = projectsPath+"web/src/main/resources/messages//";//生成的 校验出错时的信息 
	
	private static String module = "siteuser"; // 当前要生成代码的子系统（模块），根据具体业务需要进行改变
	private static String schema = "mmk_claims";
	
	public static void main(String[] args) throws SQLException, IOException {
		byte[] bs = new byte[1024 * 100];

		// 读取控制台输入，输入的是表名
		while (System.in.read(bs) != -1) {
			String params = new String(bs).trim();
			String[] tableNames = StringUtils.tokenizeToStringArray(params, delimiters);

			List<Dto> dtos = new ArrayList<Dto>();
			DBUtil db = new DBUtil(DBKey.SRC);

			for (String tableName : tableNames) {
				String tableSql = "select table_comment from tables where table_name='" + tableName
						+ "' and table_schema='"+schema+"'";
				String sql = "select COLUMN_NAME,DATA_TYPE,COLUMN_KEY,COLUMN_COMMENT,COLUMN_DEFAULT,IS_NULLABLE from COLUMNS where table_name='"
						+ tableName + "' and table_schema='"+schema+"'";

				String dtoComment = db.queryOneColumn(tableSql);

				ResultSet rs = db.query(sql);
				if (rs != null) {
					Dto dto = new Dto(module);
					dto.setComment(dtoComment);
					dto.setClassName(tableName);
					dto.setPackage_(package_);

					while (rs.next()) {
						String name = rs.getString("COLUMN_NAME");
						String type = rs.getString("DATA_TYPE");
						String key = rs.getString("COLUMN_KEY");
						String comment = rs.getString("COLUMN_COMMENT");
						String default_ = rs.getString("COLUMN_DEFAULT");
						String nullable = rs.getString("IS_NULLABLE");
						
						Field field = new Field();
						field.setName(name);
						field.setPrimary(key != null && key.equals("PRI"));
						field.setComment(comment);
						field.setDefaultValue(default_);
						field.setNullable(nullable);
						
						DataType dataType = DataType.getType(type);
						if (dataType == null)
							System.out.println(type + " not mapped.");
						if (dataType.equals(DataType.Date))
							dto.setHasDate(true);
						if (dataType.equals(DataType.BigDecimal))
							dto.setHasBigDecimal(true);
						field.setType(dataType.name());

						dto.addField(field);
					}
					rs.close();

					dtos.add(dto);
				}
			}
			db.close();
			for (Dto dto : dtos) {
				try {
					generate(dto);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TemplateException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void generate(Dto dto) throws IOException, TemplateException {

		// 生成服务接口
		IService iService = new IService(module);
		iService.setDto(dto);
		iService.setPackage_(package_);
		Project remoteProject = Project.getProject(remotePath, iService);
		MyGenrater.RemoteGenerater remoteG = new MyGenrater().new RemoteGenerater();
		remoteG.getProject(remoteProject).genarate();

		// 生成DTO
		remoteProject.setBo(dto);
		MyGenrater.DtoGenerater dtoG = new MyGenrater().new DtoGenerater();
		dtoG.getProject(remoteProject).genarate();

		// 生成DAO
		Dao dao = new Dao(module);
		dao.setDto(dto);
		dao.setPackage_(package_);
		Project soaProject = Project.getProject(soaPath, dao);
		MyGenrater.DaoGenerater daoGenerate = new MyGenrater().new DaoGenerater();
		daoGenerate.getProject(soaProject).genarate();

		// 生成服务实现
		Service service = new Service(module);
		service.setDao(dao);
		service.setPackage_(package_);
		soaProject.setBo(service);
		MyGenrater.ServiceGenerater serviceGenerate = new MyGenrater().new ServiceGenerater();
		serviceGenerate.getProject(soaProject).genarate();

		// 生成 Mybatis 配置文件
		Mapper mapper = new Mapper(module);
		mapper.setDao(dao);
		mapper.setPackage_(package_);
		soaProject.setBo(mapper);
		MyGenrater.MapperGenerater mapperGenerate = new MyGenrater().new MapperGenerater();
		mapperGenerate.getProject(soaProject).genarate();

		// 生成 spring mvc controller
		Controller controller = new Controller(module);
		controller.setService(service);
		controller.setPackage_(package_);
		Project appProject = Project.getProject(webPath, controller);
		MyGenrater.AppGenerater appGenerate = new MyGenrater().new AppGenerater();
		appGenerate.getProject(appProject).genarate();

		// 生成 表单
		Form form = new Form(module);
		form.setDto(dto);
		Project ftlProject = Project.getProject(ftlPath, form);
		MyGenrater.FormGenerater fromGenerate = new MyGenrater().new FormGenerater();
		fromGenerate.getProject(ftlProject).genarate();

		// 生成 BO校验消息
		Messages messages = new Messages(module);
		messages.setDto(dto);
		Project messagesProject = Project.getProject(messagesPath, messages);
		MyGenrater.MessageGenerater messageGenerater = new MyGenrater().new MessageGenerater();
		messageGenerater.getProject(messagesProject).genarate();
	}
}
