package com.qisen.mts.spa.service;

import java.util.List;

import com.qisen.mts.common.model.response.BaseResponse;
import com.qisen.mts.common.model.response.CommObjResponse;
import com.qisen.mts.spa.model.SessionSpa;
import com.qisen.mts.spa.model.entity.SpaAccount;
import com.qisen.mts.spa.model.request.SpaRequest;

public interface SpaAccountService {

	/**
	 * 新增spa账号
	 * @param req
	 * @return
	 */
	public BaseResponse save(SpaRequest<SpaAccount> req);
	
	/**
	 * 删除spa账号
	 * @param req
	 * @return
	 */
	public BaseResponse delete(SpaRequest<SpaAccount> req);
	
	/**
	 * 查询spa账号
	 * @param req
	 * @return
	 */
	public CommObjResponse<List<SpaAccount>> list(SpaRequest<SpaAccount> req);
	

	/**
	 * 登录
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	public CommObjResponse<SessionSpa> login(SpaRequest<SpaAccount> req) throws Exception;
}
