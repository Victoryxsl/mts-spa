package com.qisen.mts.spa.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qisen.mts.common.model.MsgCode;
import com.qisen.mts.common.model.constant.ConfigConsts;
import com.qisen.mts.common.model.response.BaseResponse;
import com.qisen.mts.common.model.response.CommObjResponse;
import com.qisen.mts.spa.dao.SpaAccountDao;
import com.qisen.mts.spa.model.SessionSpa;
import com.qisen.mts.spa.model.entity.SpaAccount;
import com.qisen.mts.spa.model.request.SpaRequest;

import net.rubyeye.xmemcached.MemcachedClient;
@Service
public class SpaAccountServiceImpl implements SpaAccountService{
	
	@Autowired
	private MemcachedClient memcachedClient;
	@Autowired
	private SpaAccountDao spaAccountDao;
	
	
	/**
	 * 新增spa账号
	 */
	@Override
	public BaseResponse save(SpaRequest<SpaAccount> req) {
		BaseResponse resp = new BaseResponse();
		SpaAccount spaAccount = req.getBody();
		int count = spaAccountDao.check(spaAccount);
		if(spaAccount.getId() != null && spaAccount.getId() > 0){
			if (count == 0 ) {
				spaAccountDao.update(spaAccount);
			}else {
				resp.setCode(MsgCode.COMMON_MOBILE_EXIST);
			}
		}else{
			if (count == 0 ) {
				spaAccountDao.create(spaAccount);
			}else {
				resp.setCode(MsgCode.COMMON_MOBILE_EXIST);
			}
		}
		return resp;
	}

	/**
	 * 删除spa账号
	 */
	@Override
	public BaseResponse delete(SpaRequest<SpaAccount> req) {
		BaseResponse resp = new BaseResponse();
		SpaAccount spaAccount = req.getBody();
		int count = spaAccountDao.delete(spaAccount);
		if (count == 0 ) {
			resp.setCode(MsgCode.COMMON_MOBILE_EXIST);
		}
		
		return resp;
	}

	@Override
	public CommObjResponse<SessionSpa> login(SpaRequest<SpaAccount> req) throws Exception {
		CommObjResponse<SessionSpa> resp = new CommObjResponse<>();
		SpaAccount spaAccount = spaAccountDao.findSpaAccount(req.getBody().getMobile());
		if (spaAccount != null) {
			if (spaAccount.getPassword().equals(req.getBody().getPassword().trim())) {
				if (spaAccount.getStatus().equals("0")) {
					SessionSpa sessionSpa = spaAccount.toJSON().toJavaObject(SessionSpa.class);
					String token = UUID.randomUUID().toString();
					sessionSpa.setToken(token);
					String sessionSpaKey = ConfigConsts.SESSION_SPA + token;
					String spaInfoJsonStr = sessionSpa.toString();
					if (!memcachedClient.add(sessionSpaKey, ConfigConsts.MAX_SESSION_USER_INTERVAL, spaInfoJsonStr)) {
						memcachedClient.replace(sessionSpaKey, ConfigConsts.MAX_SESSION_USER_INTERVAL, spaInfoJsonStr);
					}
					resp.setBody(sessionSpa);
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


	/**
	 * 查询spa账号
	 */
	@Override
	public CommObjResponse<List<SpaAccount>> list(SpaRequest<SpaAccount> req) {
		CommObjResponse<List<SpaAccount>> resp = new CommObjResponse<List<SpaAccount>>();
		SpaAccount spaAccount = req.getBody();
		List<SpaAccount>  spaList = spaAccountDao.list(spaAccount);
		resp.setBody(spaList);
		return resp;
	}
	
}
