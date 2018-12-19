package com.qisen.mts.spa.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.qisen.mts.spa.model.entity.SpaAccount;

public interface SpaAccountDao {

	/**
	 * 检查spa账号是否已存在
	 * @param spaAccount
	 * @return
	 */
	public int check(SpaAccount spaAccount);
	
	/**
	 * 删除spa账号
	 * @param spaAccount
	 * @return
	 */
	public int delete(SpaAccount spaAccount);

	/**
	 * 新增spa账号 
	 * @param spaAccount
	 */
	public void create(SpaAccount spaAccount);
	
	/**
	 * 编辑spa账号 
	 * @param spaAccount
	 */
	public void update(SpaAccount spaAccount);
	
	/**
	 * 通过手机号码查询spa账号
	 * @param mobile
	 * @return
	 */
	public SpaAccount findSpaAccount(@Param("mobile")String mobile);

	/**
	 * 查询spa账号列表
	 * @param mobile
	 * @return
	 */
	public List<SpaAccount> list(SpaAccount spaAccount);
}
