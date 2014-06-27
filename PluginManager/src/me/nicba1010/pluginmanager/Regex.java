package me.nicba1010.pluginmanager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
	public static String[] regexFirst(String regexPattern, String source,
			boolean site) {
		String[] returnStrArr = new String[2];
		if (regexPattern == null || source == null) {
			serverMessage("returned null", true);
			return null;
		}
		Pattern pattern;
		Matcher matcher;
		if (site) {
			serverMessage("Grabbing html from: " + source, true);
			URL url = null;
			try {
				url = new URL(source);
			} catch (MalformedURLException e) {
				serverMessage(e.toString(), true);
				serverMessage("returned null", true);
				return null;
			}
			URLConnection con = null;
			try {
				con = url.openConnection();
			} catch (IOException e) {
				serverMessage(e.toString(), true);
				serverMessage("returned null", true);
				return null;
			}
			Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
			Matcher m = p.matcher(con.getContentType());
			String charset = m.matches() ? m.group(1) : "ISO-8859-1";
			Reader r = null;
			try {
				r = new InputStreamReader(con.getInputStream(), charset);
			} catch (UnsupportedEncodingException e) {
				serverMessage(e.toString(), true);
				serverMessage("returned null", true);
				return null;
			} catch (IOException e) {
				serverMessage(e.toString(), true);
				serverMessage("returned null", true);
				return null;
			}
			StringBuilder buf = new StringBuilder();
			while (true) {
				int ch = 0;
				try {
					ch = r.read();
				} catch (IOException e) {
					serverMessage(e.toString(), true);
					serverMessage("returned null", true);
					return null;
				}
				if (ch < 0)
					break;
				buf.append((char) ch);
			}
			String html = buf.toString();
			serverMessage("Compiling regex pattern...", true);
			pattern = Pattern.compile(regexPattern);
			serverMessage("Matching pattern to html...", true);
			matcher = pattern.matcher(html);
			returnStrArr[0] = html;
		} else {
			serverMessage("Compiling regex pattern...", true);
			pattern = Pattern.compile(regexPattern);
			serverMessage("Matching pattern to source...", true);
			matcher = pattern.matcher(source);
			returnStrArr[0] = source;
		}
		if (matcher.find()) {
			String groupStr = matcher.group(1);
			serverMessage("Found match: " + groupStr, true);
			returnStrArr[1] = groupStr;
			return returnStrArr;
		}
		serverMessage("ERROR: NO MATCH", true);
		serverMessage("returned null", true);
		return null;
	}

	public static String[][] regexAll(String regexPattern, String source,
			boolean site) {
		String[][] returnStrArr = new String[2][];
		if (regexPattern == null || source == null) {
			serverMessage("returned null");
			return null;
		}
		Pattern pattern;
		Matcher matcher;
		if (site) {
			serverMessage("Grabbing html from: " + source);
			URL url = null;
			try {
				url = new URL(source);
			} catch (MalformedURLException e) {
				serverMessage(e.toString());
				serverMessage("returned null");
				return null;
			}
			URLConnection con = null;
			try {
				con = url.openConnection();
			} catch (IOException e) {
				serverMessage(e.toString());
				serverMessage("returned null");
				return null;
			}
			Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
			Matcher m = p.matcher(con.getContentType());
			String charset = m.matches() ? m.group(1) : "ISO-8859-1";
			Reader r = null;
			try {
				r = new InputStreamReader(con.getInputStream(), charset);
			} catch (UnsupportedEncodingException e) {
				serverMessage(e.toString());
				serverMessage("returned null");
				return null;
			} catch (IOException e) {
				serverMessage(e.toString());
				serverMessage("returned null");
				return null;
			}
			StringBuilder buf = new StringBuilder();
			while (true) {
				int ch = 0;
				try {
					ch = r.read();
				} catch (IOException e) {
					serverMessage(e.toString());
					serverMessage("returned null");
					return null;
				}
				if (ch < 0)
					break;
				buf.append((char) ch);
			}
			String html = buf.toString();
			serverMessage("Compiling regex pattern...");
			pattern = Pattern.compile(regexPattern);
			serverMessage("Matching pattern to html...");
			matcher = pattern.matcher(html);
			returnStrArr[0] = new String[] { html };
		} else {
			serverMessage("Compiling regex pattern...");
			pattern = Pattern.compile(regexPattern);
			serverMessage("Matching pattern to source...");
			matcher = pattern.matcher(source);
			returnStrArr[0] = new String[] { source };
		}
		ArrayList<String> temp = new ArrayList<String>();
		while (matcher.find()) {
			temp.add(matcher.group(1) + "-:-" + matcher.group(2));
		}
		returnStrArr[1] = temp.toArray(new String[temp.size()]);
		if (temp.size() > 0)
			return returnStrArr;
		serverMessage("ERROR: NO RESULTS");
		return (new String[0][0]);
	}

	public static void serverMessage(String msg, boolean... a) {
		System.out.println(msg);
	}
}
