package com.qisen.mts.admin.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.qisen.mts.admin.dao.AdminDao;
import com.qisen.mts.admin.dao.OrganDao;
import com.qisen.mts.admin.model.SessionAdmin;
import com.qisen.mts.admin.model.entity.Admin;
import com.qisen.mts.admin.model.entity.Organ;
import com.qisen.mts.common.model.MsgCode;
import com.qisen.mts.common.model.constant.ConfigConsts;
import com.qisen.mts.admin.model.request.AdminRequest;
import com.qisen.mts.common.model.response.BaseResponse;
import com.qisen.mts.common.model.response.CommObjResponse;

import net.rubyeye.xmemcached.MemcachedClient;
@Service
public class AdminServiceImpl implements AdminService{
	
	@Autowired
	private MemcachedClient memcachedClient;
	@Autowired
	private AdminDao adminDao;
	@Autowired
	private OrganDao organDao;
	
	@Override
	public CommObjResponse<JSONObject> list(AdminRequest<JSONObject> req) {
		CommObjResponse<JSONObject> resp = new CommObjResponse<>();
		List<Admin> admins = adminDao.list(req.getBody());
		List<Organ> organs = organDao.list(null);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("admins", admins);
		jsonObject.put("organs", organs);
		resp.setBody(jsonObject);
		return resp;
	}


	@Override
	public BaseResponse save(AdminRequest<Admin> req) {
		BaseResponse resp = new BaseResponse();
		Admin admin = req.getBody();
		int count = adminDao.check(admin);
		if (count == 0 ) {
			adminDao.create(admin);
		}else {
			resp.setCode(MsgCode.COMMON_MOBILE_EXIST);
		}
		return resp;
	}


	@Override
	public BaseResponse delete(AdminRequest<Admin> req) throws Exception {
		BaseResponse resp = new BaseResponse();
//		Admin admin = adminDao.fingById(req.getBody().getId());
		int count = adminDao.delete(req.getBody().getId());
		if (count == 0) {
			resp.setCode(MsgCode.COMMON_DELETE_FAILED);
		}
		return resp;
	}


	@Override
	public BaseResponse edit(AdminRequest<Admin> req){
		BaseResponse resp = new BaseResponse();
		int count = adminDao.check(req.getBody());
		if (count == 0 ) {
			adminDao.edit(req.getBody());
		}else{
			resp.setCode(MsgCode.COMMON_MOBILE_EXIST);
		}
		
		return resp;
	}


	@Override
	public CommObjResponse<SessionAdmin> login(AdminRequest<Admin> req) throws Exception {
		CommObjResponse<SessionAdmin> resp = new CommObjResponse<>();
		Admin admin = adminDao.findAdmin(req.getBody().getMobile());
		if (admin != null) {
			if (admin.getPasswd().equals(req.getBody().getPasswd().trim())) {
				if (admin.getAllowFlag().equals("1")) {
					SessionAdmin sa = admin.toJSON().toJavaObject(SessionAdmin.class);
					String token = UUID.randomUUID().toString();
					sa.setToken(token);
					String sessionAdminKey = ConfigConsts.SESSION_ADMIN + token;
					String adminInfoJstr = sa.toString();
					if (!memcachedClient.add(sessionAdminKey, ConfigConsts.MAX_SESSION_USER_INTERVAL, adminInfoJstr)) {
						memcachedClient.replace(sessionAdminKey, ConfigConsts.MAX_SESSION_USER_INTERVAL, adminInfoJstr);
					}
					resp.setBody(sa);
				}else{
					resp.setCode(MsgCode.ACCOUNT_FORBIDDEN);
				}
			}else{
				resp.setCode(MsgCode.ACCOUNT_PWD_NOT_MATCHED);
			}
		}else{
			resp.setCode(MsgCode.ACCOUNT_NOT_EXIST);
		}
		return resp;
	}
	
}
