package com.qisen.mts.spa.gateway;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qisen.mts.common.model.constant.ConfigConsts;
import com.qisen.mts.common.model.request.BaseRequest;
import com.qisen.mts.spa.model.SessionSpa;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

@ControllerAdvice(basePackages = { "com.qisen.mts.spa" })
public class SpaRequestBodyAdviceChain implements RequestBodyAdvice {

	private static final Logger logger = LoggerFactory.getLogger(SpaRequestBodyAdviceChain.class);

	@Autowired
	private MemcachedClient memcachedClient;

	@Override
	public boolean supports(MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
		return inputMessage;
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		if (!parameter.getMethod().getName().equals("login")) {
			BaseRequest<?> baseReq = (BaseRequest<?>) body;
			try {
//				从缓存中获取spa账号信息字符串
				String spaInfoJsonStr = memcachedClient.get(ConfigConsts.SESSION_SPA + baseReq.getToken());
//				判空
				if (StringUtils.isNotBlank(spaInfoJsonStr)) {
//					转换为json对象
					System.out.println(spaInfoJsonStr);
					JSONObject sessionSpaJson = JSON.parseObject(spaInfoJsonStr);
//					转换为java对象
					SessionSpa sessionSpa = sessionSpaJson.toJavaObject(SessionSpa.class);
//					添加到缓存中,两小时内有效
					memcachedClient.add(ConfigConsts.SESSION_SPA + baseReq.getToken(), ConfigConsts.MAX_SESSION_USER_INTERVAL, sessionSpa.toString());
//					添加到请求中
					baseReq.setSessionAccount(sessionSpa);
				}
			} catch (TimeoutException | InterruptedException | MemcachedException e) {
				logger.error(e.getMessage(), e);
			}
			return baseReq;
		}
		return body;
	}

}
