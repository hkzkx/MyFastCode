package my.CodeGenerater;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeWriter {
	private static String default_suffix = ".java";
	private Project project;

	public CodeWriter(Project project) {
		this.project = project;
	}

	public void write(String content, String suffix) throws IOException {
		String pakage = project.getBo().getPackage_();
		String fileName = project.getBo().getClassName();

		String absPath = project.getPath() + pakage.replace(".", "/");
		File dir = new File(absPath);
		if (!dir.exists())
			dir.mkdirs();

		File file = new File(absPath + "/" + fileName + (suffix == null ? default_suffix : suffix));
		StringBuffer sb = new StringBuffer();
		if(file.exists()){
			System.out.println(file.getPath() +" 已经存在，是否覆盖？yes/no");
			//至少5个字节，yes/no + 13 10
			//yes = [ x x x ,13,10]
			//no = [x x,13,10,0]
			byte[] renew = new byte[5];
			System.in.read(renew);
			
			sb.append(new String(renew));
			String input = sb.toString().toLowerCase().trim();
			if("yes".equals(input)){
				FileWriter fw = new FileWriter(file);
				fw.write(new String(content.getBytes("utf-8")));
				fw.flush();
				fw.close();
			}else if("no".equals(input)){
				File dest = new File(absPath + "/" + fileName + (suffix == null ? default_suffix : suffix)+".bak."+System.currentTimeMillis());
				file.renameTo(dest);
				FileWriter fw = new FileWriter(file);
				fw.write(new String(content.getBytes("utf-8")));
				fw.flush();
				fw.close();
			}else{
				System.out.println("输入错误");
			}
		}else{
			FileWriter fw = new FileWriter(file);
			fw.write(new String(content.getBytes("utf-8")));
			fw.flush();
			fw.close();
		}
		
		
	}

	public void write(String content) throws IOException {
		write(content, null);
	}
}
