package me.nicba1010.pluginmanager;

import static me.nicba1010.pluginmanager.Regex.regexAll;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RGX {
	static String					newestDlLink;
	static HashMap<String, Boolean>	canDL	= new HashMap<String, Boolean>();

	public static void main(String[] args) throws IOException {
		// <dt>Size</dt>
		// <dd>11.9 KiB</dd>
		// "<td class=\"col-search-entry\"><h2><a href=\"(.*)\">.*</a></h2></td>"
		// getJarFromBukkitDev();
		// ArrayList<String[]> results = bukkitSearch("buycraft", 1);
		// for (String[] strings : results) {
		// System.out.println((results.indexOf(strings) + 1) + ". "
		// + strings[0]);
		// }
		// Commons.unZipIt(("C:\\as\\plugins\\Essentials.zip"),
		// "C:\\as\\plugins\\");
		// canDL.put("Nicba1010", true);
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// while (canDL.get("Nicba1010")) {
		// long start = System.currentTimeMillis();
		// long end = System.currentTimeMillis();
		// System.out.println(end - start);
		// }
		// }
		// }).start();
		// @SuppressWarnings("resource")
		// Scanner scanner = new Scanner(System.in);
		// String la=scanner.nextLine();
		// System.out.println(la);
		// canDL.put("Nicba1010", false);
		// System.out.println(canDL.get("Nicba1010"));
		String file = (new String(Files.readAllBytes(new File("C:\\as\\plugins\\KitsPlusPlus" + File.separator + "config.yml").toPath())).replaceAll(" ", "").replaceAll("\r|\n", "")
			.replaceAll(System.getProperty("line.separator"), "").replace("kit:", ""));
		Pattern pattern = Pattern.compile("([^:']*):");
		Matcher matcher = pattern.matcher(file);
		while (matcher.find()) {
			System.out.println(matcher.group(1));
		}
	}

	static String[]				flags	= new String[] { "-gui", "-g" };

	static ArrayList<String[]>	results	= new ArrayList<String[]>();

	public static ArrayList<String[]> bukkitSearch(String search, Integer... a) {
		results.clear();
		Integer page = a.length > 0 ? a[0] : 0;
		for (String string : regexAll("<td class=\"col-search-entry\"><h2><a href=\"/bukkit-plugins/(.*)/\">(.*)</a></h2></td>", "http://dev.bukkit.org/search/?page=" + page
			+ "&scope=projects&search=" + search, true)[1]) {
			string = string.replaceAll("<(?:/)?mark>", "").replaceAll("  ", "");
			String[] arr = string.split("-:-");
			results.add(arr);
		}
		return results;
	}

	@SuppressWarnings("unused")
	private static String getJarFromBukkitDev() {
		newestDlLink = "http://dev.bukkit.org/bukkit-plugins" + regexFirst("<a href=\"/bukkit-plugins(.*)\">Download</a>", "http://dev.bukkit.org/bukkit-plugins/enchantgui/", true);
		return "http://dev.bukkit.org/media/files" + regexFirst("<a href=\"http://dev.bukkit.org/media/files(.*)jar\">", newestDlLink, true) + "jar";
	}

	public static String regexFirst(String regexPattern, String source, boolean site) {
		if (regexPattern == null || source == null)
			return null;
		Pattern pattern;
		Matcher matcher;
		if (site) {
			serverMessage("Grabbing html from: " + source);
			URL url = null;
			try {
				url = new URL(source);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			URLConnection con = null;
			try {
				con = url.openConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
			Matcher m = p.matcher(con.getContentType());
			String charset = m.matches() ? m.group(1) : "ISO-8859-1";
			Reader r = null;
			try {
				r = new InputStreamReader(con.getInputStream(), charset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			StringBuilder buf = new StringBuilder();
			while (true) {
				int ch = 0;
				try {
					ch = r.read();
				} catch (IOException e) {
					e.printStackTrace();
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
		} else {
			serverMessage("Compiling regex pattern...");
			pattern = Pattern.compile(regexPattern);
			serverMessage("Matching pattern to source...");
			matcher = pattern.matcher(source);
		}
		if (matcher.find()) {
			String groupStr = matcher.group(1);
			serverMessage("Found match: " + groupStr);
			return groupStr;
		}
		serverMessage("ERROR: NO MATCH");
		return null;
	}

	private static void serverMessage(String msg) {
		System.out.println(msg);
		// Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say "+ msg);
	}
}
