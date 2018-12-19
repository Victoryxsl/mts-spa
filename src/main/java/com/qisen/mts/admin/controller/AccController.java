package com.qisen.mts.admin.controller;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qisen.mts.common.model.entity.sys.Account;
import com.qisen.mts.admin.model.request.AdminRequest;
import com.qisen.mts.common.model.request.PageRequest;
import com.qisen.mts.common.model.response.BaseResponse;
import com.qisen.mts.common.model.response.PageResponse;
import com.qisen.mts.common.service.sys.AccountService;

@Controller
@RequestMapping("/admin/account")
public class AccController {

	@Autowired
	private AccountService accountService;

	@ApiOperation( value = "更新登录状态或创建用户")
	@RequestMapping("/save")
	@ResponseBody
	public BaseResponse save(@RequestBody AdminRequest<Account> req) throws Exception {
		return accountService.save(req);
	}

	@RequestMapping("/status")
	@ResponseBody
	public BaseResponse status(@RequestBody AdminRequest<JSONObject> req) throws Exception {
		return accountService.status(req);
	}

	@RequestMapping("/list")
	@ResponseBody
	public PageResponse<List<Account>> list(@RequestBody PageRequest<JSONObject> req) {
		return accountService.list(req);
	}
	
	@RequestMapping("/rights")
	@ResponseBody
	public BaseResponse rights(@RequestBody AdminRequest<Account> req) throws Exception {
		return accountService.rights(req);
	}

}

