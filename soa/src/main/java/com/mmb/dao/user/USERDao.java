package com.mmb.dao.user;

import com.mmb.annotation.RegisterDto;
import com.basedao.common.BaseDao;
import com.mmb.model.user.USER;

@RegisterDto(USER.class)
public interface USERDao extends BaseDao<USER>{

}
