package com.qisen.mts.spa.gateway;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartResolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qisen.mts.common.model.ResultCode;
import com.qisen.mts.common.model.constant.ConfigConsts;
import com.qisen.mts.common.model.response.BaseResponse;
import com.qisen.mts.gateway.BodyReaderHttpServletRequestWrapper;
import com.qisen.mts.gateway.HttpHelper;
import com.qisen.mts.gateway.SpringContextUtil;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

@Component("spaTokenFilter")
public class TokenFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);
	
	private MultipartResolver multipartResolver;
	@Autowired
	private MemcachedClient memcachedClient;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		ServletRequest requestWrapper = null;
		JSONObject baseReq = null;
		String token = null;
//		未知上传什么内容使用，先保留--chali
		if (request.getRequestURI().contains("/spa/upload") || request.getRequestURI().contains("/spa/import")) {
			if (multipartResolver == null)
				multipartResolver = (MultipartResolver) SpringContextUtil.getBean("multipartResolver");
			String lang = null;
			if (multipartResolver.isMultipart(request)) {
				// 防止流读取一次后就没有了
				requestWrapper = multipartResolver.resolveMultipart(request);
				token = requestWrapper.getParameter("token");
				lang = requestWrapper.getParameter("lang");
			} else {
				token = request.getParameter("token");
				lang = request.getParameter("lang");
			}
			requestWrapper.setAttribute("lang", lang);
		} else {
			// 防止流读取一次后就没有了
			requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
			String body = HttpHelper.getBodyString(requestWrapper);
			baseReq = JSON.parseObject(body);
			token = baseReq.getString("token");
			String lang = baseReq.getString("lang");
			requestWrapper.setAttribute("lang", lang);
			logger.debug(baseReq.toJSONString());
		}
		
		if (!request.getRequestURI().contains("/spa/account/login")) {
			BaseResponse resp = new BaseResponse();
			if (StringUtils.isNotBlank(token)) {
				try {
					String sessionAdminKey = ConfigConsts.SESSION_SPA + token;
					String adminInfoJstr = memcachedClient.get(sessionAdminKey);
					if (StringUtils.isNotBlank(adminInfoJstr)) {
						memcachedClient.replace(sessionAdminKey, ConfigConsts.MAX_SESSION_USER_INTERVAL, adminInfoJstr);
					} else
						resp.setResult(ResultCode.INVALID_TOKEN);
				} catch (TimeoutException | InterruptedException | MemcachedException e) {
					logger.error("filter error:", e);
					resp.setResult(ResultCode.FAILED);
				}
			} else
				resp.setResult(ResultCode.INVALID_PARAMETERS);

			if (resp.getResult() != ResultCode.SUCCESS.getCode()) {
				request.setCharacterEncoding("UTF-8");
				response.setContentType("application/json;charset=UTF-8");
				// 未登录
				PrintWriter out = response.getWriter();
				out.print(resp.toString());
				out.close();
				return;
			}
		}
		chain.doFilter(requestWrapper, response);
	}

}
