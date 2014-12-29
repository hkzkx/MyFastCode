package ${service.package_};

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ${service.dao.package_}.${service.dao.className};
import ${service.dao.dto.package_}.${service.dao.dto.className};

import com.basedao.common.BaseDao;
import com.basedao.common.BaseService;

@Service
public class ${service.className} extends BaseService<${service.dao.dto.className}> implements ${service.iClassName} {

	protected Log logger = LogFactory.getLog(getClass());

	@Autowired
	private ${service.dao.className} ${service.dao.instanceName};

	
	public BaseDao<${service.dao.dto.className}> getDao() {
		return ${service.dao.instanceName};
	}
	
	
}
