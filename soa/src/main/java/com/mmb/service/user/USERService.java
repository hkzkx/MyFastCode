package com.mmb.service.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmb.dao.user.USERDao;
import com.mmb.model.user.USER;

import com.basedao.common.BaseDao;
import com.basedao.common.BaseService;

@Service
public class USERService extends BaseService<USER> implements IUSERService {

	protected Log logger = LogFactory.getLog(getClass());

	@Autowired
	private USERDao uSERDao;

	
	public BaseDao<USER> getDao() {
		return uSERDao;
	}
	
	
}
