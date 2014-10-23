package com.yun.spider;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HttpUtils {

	/**
	 * 页面重定向
	 * 
	 * @param url
	 * @return
	 */
	public static String HttpRedirect(String url) {
		URL obj = null;
		HttpURLConnection conn = null;
		try {
			obj = new URL(url.trim());
			conn = (HttpURLConnection) obj.openConnection();
			conn.setReadTimeout(5000);
			conn.setRequestMethod("GET");
			boolean redirect = false;
			int status = conn.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK
					|| status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER) {
				redirect = true;
			}
			if (redirect) {
				if (status == 505) {
					url = conn.getURL().toString().replace(" ", "");
					conn = (HttpURLConnection) new URL(url).openConnection();
					String charset = conn.getContentType();
					System.out.println(charset);
				}
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "GBK"));
			String inputLine;
			StringBuffer html = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				html.append(inputLine);
			}
			in.close();
			return html.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过网站域名URL获取该网站的源码
	 * 
	 * @param url
	 * @return String
	 * @throws Exception
	 */
	public static String getURLSource(String str) throws Exception {
		HttpClient client = new HttpClient();
		// 创建Get方法实例
		GetMethod method = new GetMethod(str);
		client.executeMethod(method);
		InputStream inStream = method.getResponseBodyAsStream(); // 通过输入流获取html二进制数据
		byte[] data = readInputStream(inStream); // 把二进制数据转化为byte字节数据
		String htmlSource = new String(data);
		method.releaseConnection();
		return htmlSource;
	}

	/**
	 * 把二进制流转化为byte字节数组
	 * 
	 * @param instream
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream instream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1204];
		int len = 0;
		while ((len = instream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		instream.close();
		return outStream.toByteArray();
	}

	public static void HttpClientPost(String uri, String host, Integer port,
			String json) {
		HttpClient client = new HttpClient();
		client.getHostConfiguration().setHost(host, port, "http");
		PostMethod post = new PostMethod(uri);
		NameValuePair simcard = new NameValuePair("json", json);
		post.setRequestBody(new NameValuePair[] { simcard });
		try {
			client.executeMethod(post);
			post.releaseConnection();
			System.out.println(post.getStatusLine()); // 打印结果页面
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String HttpGetApache(String url, String name, String value) {
		try {
			BufferedReader in = null;
			StringBuffer result = new StringBuffer();
			HttpClient httpclient = new HttpClient();
			GetMethod getMethod = new GetMethod(url);
			NameValuePair[] pairs = new NameValuePair[1];
			pairs[0] = new NameValuePair(name,
					URLEncoder.encode(value, "UTF-8"));
			getMethod.setQueryString(pairs);
			httpclient.executeMethod(getMethod);
			InputStream input = getMethod.getResponseBodyAsStream();
			String line = "";
			in = new BufferedReader(new InputStreamReader(input));
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			getMethod.releaseConnection();
			String json = URLEncoder.encode(result.toString(), "UTF-8");
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String HttpGetApache(String url, NameValuePair[] pairs) {
		try {
			BufferedReader in = null;
			StringBuffer result = new StringBuffer();
			HttpClient httpclient = new HttpClient();
			GetMethod getMethod = new GetMethod(url);
			// NameValuePair[] pairs = new NameValuePair[1];
			// pairs[0] = new NameValuePair(name,
			// URLEncoder.encode(value, "UTF-8"));
			if (pairs != null) {
				getMethod.setQueryString(pairs);
			}
			httpclient.executeMethod(getMethod);
			InputStream input = getMethod.getResponseBodyAsStream();
			String line = "";
			in = new BufferedReader(new InputStreamReader(input));
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			System.out.println(result.toString());
			getMethod.releaseConnection();
			String json = URLEncoder.encode(result.toString(), "UTF-8");
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setHeaders(HttpMethod method) {
		method.setRequestHeader("Accept-Language", "zh-cn");
		method.setRequestHeader("Referer", "http://www.itbt.com.cn/");
		method.setRequestHeader("Accept-Encoding", "gzip, deflate");
		method.setRequestHeader(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0;"
						+ "Maxthon; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		method.setRequestHeader("Host", "www.itbt.com.cn");
		method.setRequestHeader("Connection", " Keep-Alive");
		method.setRequestHeader("Maxthon",
				".NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		method.setRequestHeader("Content-Length", "49");
		method.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");
	}

	public static String HttpPostApache(String url) {
		try {
			BufferedReader in = null;
			StringBuffer result = new StringBuffer();
			HttpClient httpclient = new HttpClient();
			PostMethod getMethod = new PostMethod(url);
			// setHeaders(getMethod);
			// 传递post参数
			getMethod.setParameter("sel[selecttype]", "3");
			getMethod.setParameter("mode", "1");
			getMethod.setParameter("tpage[page]", "1");
			getMethod.setParameter("tpage[size]", "1");

			httpclient.executeMethod(getMethod);
			InputStream input = getMethod.getResponseBodyAsStream();
			String line = "";
			in = new BufferedReader(new InputStreamReader(input, "utf-8"));
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			getMethod.releaseConnection();
			System.out.println(result.toString());
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param urlStr
	 * @param params
	 *            请求参数，请求参数应该是name1=value1&name2=value2的形式。
	 * @return
	 */
	public static String sendUrl(String url, String params) {
		// urlStr = "http://127.0.0.1/cms/sycn/sycnPageData";
		// host = "127.0.0.1";
		// port = 80;
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			// conn.setRequestProperty("Pragma:", "no-cache");
			// conn.setRequestProperty("Cache-Control", "no-cache");
			// conn.setRequestProperty("Content-Type", "text/xml");
			// conn.setRequestProperty("ContentType","text/xml;charset=utf-8");
			conn.setRequestProperty("charset", "UTF-8");
			conn.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT //5.1)AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.46 Safari/535.11");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			if (params != null) {
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				out.print(params);
				// flush输出流的缓冲
				out.flush();
			}
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "出现异常";
		} finally {
			try {
				if (out != null) {
					out.close();
					out = null;
				}
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}

	public static String jsoupPost(String url) {
		try {
			Document doc = Jsoup
					.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13")
					.timeout(1000 * 10).ignoreContentType(true).post();
			String json = doc.select("body").text();
			return json;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检查是否有秒标
	 * 
	 * @param content
	 */
	public static boolean checkMiao() {
		String json = HttpUtils
				.HttpPostApache("http://www.itbt.com.cn/index.php?s=/Invest/GetBorrowlist");
		if (json != null && json.indexOf("user_id") != -1) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		checkMiao();
	}

}
