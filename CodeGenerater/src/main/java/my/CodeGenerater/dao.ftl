package ${dao.package_};

import com.code.annotation.RegisterDto;
import com.basedao.common.BaseDao;
import ${(dao.dto.package_)!}.${(dao.dto.className)!};

@RegisterDto(${(dao.dto.className)!}.class)
public interface ${(dao.className)!} extends BaseDao<${(dao.dto.className)!}>{

}
