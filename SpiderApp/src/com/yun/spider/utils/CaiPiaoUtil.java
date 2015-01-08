package com.yun.spider.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.yun.spider.bean.Caipiao;

public class CaiPiaoUtil {

	static Integer countTotal = 0;

	/**
	 * 页面重定向
	 * 
	 * @param url
	 * @return
	 */
	public static String HttpRedirect(String url, String code) {
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
					conn.getInputStream(), code));
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

	public static class NumBean {
		String wei;// 以什么数字计算
		String nums;// 数字数组
		Integer weishu;// 出现在第几位
		String weizhi;// 个，十，百，千，万
		Integer weiCount;
		/**
		 * 计算每次出现 数组的次数
		 */
		Map<Integer, Integer> count;

		public NumBean(String wei, String nums) {
			this.wei = wei;
			this.nums = nums;
		}

		public String getWei() {
			return wei;
		}

		public void setWei(String wei) {
			this.wei = wei;
		}

		public String getNums() {
			return nums;
		}

		public void setNums(String nums) {
			this.nums = nums;
		}

		public Integer getWeishu() {
			return weishu;
		}

		public void setWeishu(Integer weishu) {
			this.weishu = weishu;
		}

		public String getWeizhi() {
			return weizhi;
		}

		public void setWeizhi(String weizhi) {
			this.weizhi = weizhi;
		}

		public Map<Integer, Integer> getCount() {
			return count;
		}

		public void setCount(Map<Integer, Integer> count) {
			this.count = count;
		}

		public Integer getWeiCount() {
			return weiCount;
		}

		public void setWeiCount(Integer weiCount) {
			this.weiCount = weiCount;
		}

	}

	public static Integer newPeriod() {
		String url = "http://trend.caipiao.163.com/cqssc/jiben-5xing.html?periodNumber=30";
		String str = HttpRedirect(url, "utf-8");
		try {
			str = new String(str.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(str);
		Element e = doc.select(".c_ba2636").last();
		String period = e.attr("kjhm");
		if (period != null) {
			return Integer.valueOf(period);
		}
		return null;
	}

	public static List<Caipiao> caipiaoList(Integer beginPeriod,
			Integer endPeriod) {
		String url = "http://trend.caipiao.163.com/cqssc/jiben-5xing.html?beginPeriod="
				+ beginPeriod + "&endPeriod=" + endPeriod;
		if (beginPeriod == null && endPeriod == null) {
			url = "http://trend.caipiao.163.com/cqssc/jiben-5xing.html?beginPeriod=&endPeriod=";
		}
		List<Caipiao> list = new ArrayList<Caipiao>();
		// "http://trend.caipiao.163.com/cqssc/jiben-5xing.html?selectDate=&beginPeriod=&endPeriod=";

		String str = HttpRedirect(url, "utf-8");
		String wan = "";
		String qian = "";
		String bai = "";
		String shi = "";
		String ge = "";
		try {
			str = new String(str.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(str);
		Elements eles = doc.select(".c_ba2636");
		for (Element e : eles) {
			Caipiao caipiao = new Caipiao();
			String hao = e.text().trim();
			if (isNumeric(hao)) {
				String period = e.attr("kjhm");
				caipiao.setQishu(Integer.valueOf(period));
				wan = hao.substring(0, 1);
				qian = hao.substring(1, 2);
				bai = hao.substring(2, 3);
				shi = hao.substring(3, 4);
				ge = hao.substring(4, 5);
				caipiao.setGe(Integer.valueOf(ge));
				caipiao.setShi(Integer.valueOf(shi));
				caipiao.setBai(Integer.valueOf(bai));
				caipiao.setQian(Integer.valueOf(qian));
				caipiao.setWan(Integer.valueOf(wan));
				list.add(caipiao);
			}
		}
		return list;
	}

	public static String total(String weishuP, String numsP) {
		countTotal = 0;
		// String url =
		// "http://trend.caipiao.163.com/cqssc/jiben-5xing.html?selectDate=&beginPeriod=&endPeriod=";
		String url = "http://trend.caipiao.163.com/cqssc/jiben-5xing.html?periodNumber=100";
		String str = HttpRedirect(url, "utf-8");
		String wan = "";
		String qian = "";
		String bai = "";
		String shi = "";
		String ge = "";
		try {
			str = new String(str.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(str);
		Elements eles = doc.select(".c_ba2636");
		for (Element e : eles) {
			String hao = e.text().trim();
			if (isNumeric(hao)) {
				wan += hao.substring(0, 1);
				qian += hao.substring(1, 2);
				bai += hao.substring(2, 3);
				shi += hao.substring(3, 4);
				ge += hao.substring(4, 5);
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("万位", wan);
		map.put("千位", qian);
		map.put("百位", bai);
		map.put("十位", shi);
		map.put("个位", ge);

		NumBean jiuBean = new NumBean(weishuP, numsP);

		List<NumBean> listBean = new ArrayList<CaiPiaoUtil.NumBean>();
		listBean.add(jiuBean);
		// listBean.add(siBean);
		HashMap<Integer, Integer> countMap = new HashMap<Integer, Integer>();
		for (Map.Entry<String, String> e : map.entrySet()) {
			for (NumBean bean : listBean) {
				bean.setWeizhi(e.getKey());
				String nums = e.getValue();
				// System.out.println(nums);
				String temp = nums.replaceAll(bean.wei, "");
				bean.setWeiCount(nums.length() - temp.length());
				Integer jiuwei = nums.lastIndexOf(bean.getWei());
				if (jiuwei != -1) {
					// System.out.println(jiuwei);
					boolean check = true;
					String houwei = nums.substring(jiuwei + 1);
					int weishu = 0;
					for (int j = 0; j < houwei.length(); j++) {
						String num = houwei.substring(j, j + 1);
						if (bean.nums.indexOf(num + ",") != -1) {
							check = false;
							weishu = j;
							break;
						}
					}
					if (check) {
						System.out.println(bean.wei + ":" + e.getKey() + ":"
								+ (nums.substring(jiuwei).length()) + "位数:"
								+ (weishu + 1));
					}
					// System.out.println(bean.getWeizhi() + nums + "wei"
					// + bean.getWei()+bean.getWeiCount());
					bean.setCount(totalCount(bean.getWei(), nums,
							bean.getNums()));
					jia(countMap, bean.getCount());
				}
			}

		}

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<Integer, Integer> m : countMap.entrySet()) {
			System.out.println("位置:" + m.getKey() + "出现次数:" + m.getValue()
					+ "概率:" + (m.getValue().doubleValue() / countTotal) * 100);

			sb.append("位置:" + m.getKey() + "出现次数:" + m.getValue() + "概率:"
					+ (m.getValue().doubleValue() / countTotal) * 100);
		}
		System.out.println("总的次数:" + countTotal);
		sb.append("总的次数:" + countTotal);
		return sb.toString();
	}

	public static void total() {
		// String url =
		// "http://trend.caipiao.163.com/cqssc/jiben-5xing.html?selectDate=&beginPeriod=&endPeriod=";
		String url = "http://trend.caipiao.163.com/cqssc/jiben-5xing.html?periodNumber=100";
		String str = HttpRedirect(url, "utf-8");
		String wan = "";
		String qian = "";
		String bai = "";
		String shi = "";
		String ge = "";
		try {
			str = new String(str.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(str);
		Elements eles = doc.select(".c_ba2636");
		for (Element e : eles) {
			String hao = e.text().trim();
			if (isNumeric(hao)) {
				wan += hao.substring(0, 1);
				qian += hao.substring(1, 2);
				bai += hao.substring(2, 3);
				shi += hao.substring(3, 4);
				ge += hao.substring(4, 5);
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("万位", wan);
		map.put("千位", qian);
		map.put("百位", bai);
		map.put("十位", shi);
		map.put("个位", ge);

		NumBean jiuBean = new NumBean("9", "2,3,4,");
		NumBean siBean = new NumBean("4", "2,3,4,");

		List<NumBean> listBean = new ArrayList<CaiPiaoUtil.NumBean>();
		listBean.add(jiuBean);
		listBean.add(siBean);
		HashMap<Integer, Integer> countMap = new HashMap<Integer, Integer>();
		for (Map.Entry<String, String> e : map.entrySet()) {
			for (NumBean bean : listBean) {
				bean.setWeizhi(e.getKey());
				String nums = e.getValue();
				// System.out.println(nums);
				String temp = nums.replaceAll(bean.wei, "");
				bean.setWeiCount(nums.length() - temp.length());
				Integer jiuwei = nums.lastIndexOf(bean.getWei());
				if (jiuwei != -1) {
					// System.out.println(jiuwei);
					boolean check = true;
					String houwei = nums.substring(jiuwei + 1);
					int weishu = 0;
					for (int j = 0; j < houwei.length(); j++) {
						String num = houwei.substring(j, j + 1);
						if (bean.nums.indexOf(num + ",") != -1) {
							check = false;
							weishu = j;
							break;
						}
					}
					if (check) {
						System.out.println(bean.wei + ":" + e.getKey() + ":"
								+ (nums.substring(jiuwei).length()) + "位数:"
								+ (weishu + 1));
					}
					// System.out.println(bean.getWeizhi() + nums + "wei"
					// + bean.getWei()+bean.getWeiCount());
					bean.setCount(totalCount(bean.getWei(), nums,
							bean.getNums()));
					jia(countMap, bean.getCount());
				}
			}

		}
		for (Map.Entry<Integer, Integer> m : countMap.entrySet()) {
			System.out.println("位置:" + m.getKey() + "出现次数:" + m.getValue()
					+ "概率:" + (m.getValue().doubleValue() / countTotal) * 100);
		}
		System.out.println("总的次数:" + countTotal);
	}

	public static Map<Integer, Integer> jia(Map<Integer, Integer> count,
			Map<Integer, Integer> map) {
		for (Map.Entry<Integer, Integer> e : map.entrySet()) {
			Integer times = e.getValue();
			if (count.get(e.getKey()) == null) {
				count.put(e.getKey(), times);
			} else {
				count.put(e.getKey(), count.get(e.getKey()) + times);
			}
		}
		return count;
	}

	public static void main(String[] args) {
		// totalCount("4", "772321779271196923147884446650", "2,3,4,");
		total();
	}

	/**
	 * 判断是否是数字
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNumeric(String strValue) {
		try {
			Long temp = Long.valueOf(strValue);
			if (temp != null && temp >= 0) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}

	public static Map<Integer, Integer> totalCount(String wei, String nums,
			String numArr) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < 100000; i++) {
			int index = nums.indexOf(wei);
			if (index != -1) {
				nums = nums.substring(index + 1);
				for (int j = 0; j < nums.length(); j++) {
					String num = nums.substring(j, j + 1);
					if (numArr.indexOf(num + ",") != -1) {
						Integer temp = map.get(j + 1);
						if (temp == null) {
							map.put(j + 1, 1);
						} else {
							map.put(j + 1, temp + 1);
						}
						countTotal += 1;
						break;
					}
				}
			} else {
				break;
			}
		}
		/*
		 * for (Map.Entry<Integer, Integer> e : map.entrySet()) {
		 * System.out.println(e.getKey() + "count" + e.getValue()); }
		 */
		return map;
	}
}
