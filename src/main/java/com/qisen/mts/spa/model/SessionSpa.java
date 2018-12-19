package com.qisen.mts.spa.model;

import java.util.Date;

import com.qisen.mts.common.model.SessionAccount;

public class SessionSpa extends SessionAccount {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3920174407288686753L;

//	@Override
//	public String toString() {
//		return "SessionSpa [id=" + id + ", name=" + name + ", mobile=" + mobile + ", createDate=" + createDate
//				+ ", menuStr=" + menuStr + ", optStr=" + optStr + ", role=" + role + ", status=" + status + ", eid="
//				+ eid + ", sid=" + sid + "]";
//	}
	private Integer id;
	private String name;
	private String mobile;// 手机号码
	private Date createDate;// 创建日期
	private String menuStr;// 菜单权限
	private String optStr;// 操作权限
	private String role;// 角色:0超级管理员,1管理员,2操作员
	private String status;//状态:0正常,1禁用,2删除

	private Integer eid;
	public Integer getEid() {
		return eid;
	}
	public void setEid(Integer eid) {
		this.eid = eid;
	}
	public Integer getSid() {
		return sid;
	}
	public void setSid(Integer sid) {
		this.sid = sid;
	}
	private Integer sid;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getMenuStr() {
		return menuStr;
	}
	public void setMenuStr(String menuStr) {
		this.menuStr = menuStr;
	}
	public String getOptStr() {
		return optStr;
	}
	public void setOptStr(String optStr) {
		this.optStr = optStr;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}
