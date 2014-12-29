package com.code.dao.user;

import com.code.annotation.RegisterDto;
import com.basedao.common.BaseDao;
import com.code.model.user.SitUser;

@RegisterDto(SitUser.class)
public interface SitUserDao extends BaseDao<SitUser>{

}
