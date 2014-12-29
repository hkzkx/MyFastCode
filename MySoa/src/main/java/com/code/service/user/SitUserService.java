package com.code.service.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.code.dao.user.SitUserDao;
import com.code.model.user.SitUser;

import com.basedao.common.BaseDao;
import com.basedao.common.BaseService;

@Service
public class SitUserService extends BaseService<SitUser> implements ISitUserService {

	protected Log logger = LogFactory.getLog(getClass());

	@Autowired
	private SitUserDao sitUserDao;

	
	public BaseDao<SitUser> getDao() {
		return sitUserDao;
	}
	
	
}
