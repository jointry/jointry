package jp.ac.aiit.jointry.services.broker.core;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * URLの文字列表現から内部情報を作り保持する。
 * <p>URLの例: http://localhost:8080/index.html?userid="Takashi"&password="yt"
 */
class URLInfo {
	private URL url;
	private int port;
	private Map<String,String> queryParamMap;

	public URLInfo(String urlpath) throws MalformedURLException {
		url = new URL(urlpath);
		port = url.getPort();
		if(port < 0) port = 80;
		parseQueryParams(url.getQuery());
	}
	private void parseQueryParams(String params) {
	    queryParamMap = new HashMap<String,String>();
		if(params != null) {
			String[] paramArray = params.split("&");
			for(String param : paramArray) {
				String[] pair = param.split("=");
				queryParamMap.put(pair[0], pair[1]);
			}
		}
	}
	public String getProtocol() { return url.getProtocol(); }
	public String getHost()     { return url.getHost(); }
	public int    getPort()     { return port; }
	public String getPath()     { return url.getPath(); }
	public String getQuery(String var) { return queryParamMap.get(var); }

	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("URLInfo[" + getProtocol() + "://" +
				  getHost() + ":" + getPort() + getPath() + "]\n");
		for(Map.Entry<String,String> e : queryParamMap.entrySet())
			sb.append(e.getKey() + " = " + e.getValue() + "\n");
		return sb.toString();
	}
}		
