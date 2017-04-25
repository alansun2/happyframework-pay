package com.ehu.pay.weixin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import com.ehu.pay.config.EhPayConfig;
import com.ehu.pay.constants.PayResultCodeConstants;
import com.ehu.pay.constants.PayResultMessageConstants;
import com.ehu.pay.exception.PayException;
import com.ehu.pay.weixin.client.TenpayHttpClient;


public class WeChatUtils {
	private static Logger log = Logger.getLogger(WeChatUtils.class);
	
	public static Map<String,String> getResponseInfo(SortedMap<String, String> map,String requestUrl) {
		Map<String,String> resultMap = null;
		String params = getXmlString(map);//map转String
		TenpayHttpClient httpClient = new TenpayHttpClient();
		httpClient.setReqContent(requestUrl);
		String resContent = "";
		if (httpClient.callHttpPost(requestUrl, params)) {
			resContent = httpClient.getResContent();
			try {
				resultMap = XMLUtil.doXMLParse(resContent);
			} catch (JDOMException e) {
				log.error("xml解析错误"); //(log, resContent, "xml解析有误");   //"xml解析有误");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultMap;
	}
	/**
	 * 微信签名并返回带签名的map
	 * @param map
	 * @param config
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static SortedMap createSign(SortedMap map,EhPayConfig config) {
		StringBuffer sb = new StringBuffer();
		Set es = map.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key="+config.getWxPay_app_key());
		String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
		map.put("sign", sign);
		return map;
	}
	/**
	 * 使用证书发送请求到微信服务器
	 * @param map
	 * @param requestUrl
	 * @param code
	 * @return
	 */
	public static Map<String,String> wechatPostWithSSL(SortedMap<String, String> map,String requestUrl,String code){
		CloseableHttpClient httpclient = null;
		Map<String,String> resultMap = null;
		String xmlString = getXmlString(map);
		try {
			SSLConnectionSocketFactory sslsf = WeChatSSlUtil.getSSL(code);
			httpclient = HttpClients.custom()
					.setSSLSocketFactory(sslsf)
					.build();
			HttpPost httppost = new HttpPost(requestUrl);
			StringEntity myEntity = new StringEntity(xmlString, "UTF-8");
            httppost.addHeader("Content-Type", "text/xml"); 
			httppost.setEntity(myEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				log.info(response.getStatusLine());
				if (entity != null) {
					System.out.println("Response content length: " + entity.getContentLength());
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
					String text;
					StringBuffer sb = new StringBuffer();
					while ((text = bufferedReader.readLine()) != null) {
						sb.append(text);
					}
					resultMap = XMLUtil.doXMLParse(sb.toString());
				}
				EntityUtils.consume(entity);
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(resultMap==null)
			return null;
		else
			return resultMap;
	}
	public static boolean checkWechatResponse(Map<String,String> map) throws PayException{
		boolean flag = false;
		if(map.isEmpty()){
			log.error("WeChatUtils - checkWechatResponse 微信返回有误");
			return false;
		}
		Iterator<Entry<String,String>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,String> entry = it.next();
			log.info(entry.getKey()+":::"+entry.getValue());
		}
		if(map.containsKey("return_code")&&"SUCCESS".equals(map.get("return_code"))){
			if("SUCCESS".equals(map.get("result_code"))){
				flag = true;
			}else if("FAIL".equals(map.get("result_code"))){
				log.info(map.get("err_code")+":::"+map.get("err_code_des"));
			}else{
				throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10004,PayResultMessageConstants.STRING_WECHATPAY_10004);
			}
		}else if(map.containsKey("return_code")&&"FAIL".equals(map.get("return_code"))){
			log.info("return_message为：：："+map.get("return_msg"));
		}
		return flag;
	}
	
	protected static String getXmlString(SortedMap<String, String> map){
		StringBuffer sb = new StringBuffer();
		Set es = map.entrySet();
		Iterator it = es.iterator();
		sb.append("<xml>");
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !"".equals(v) && !"appkey".equals(k)) {
				sb.append("<" + k + ">" + v + "</" + k + ">");
			}
		}
		String params = sb.append("</xml>").toString();
		log.info(params);
		return params;
	}
}
