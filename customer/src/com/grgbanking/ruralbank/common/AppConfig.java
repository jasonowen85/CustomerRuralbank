package com.grgbanking.ruralbank.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.grgbanking.ruralbank.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 *
 */
public class AppConfig {
	private static Properties properties;
	public static String LOCAL_PATH = "/config.properties";

	private AppConfig() {
	}

	private static class SingletonHolder {
		/**
		 * 静态初始化器，由JVM来保证线程安全
		 */
		private static AppConfig instance = new AppConfig();
	}

	public static AppConfig getAppConfig(Context context) {
		LOCAL_PATH = context.getFilesDir().getPath() + "/config.properties";
		return SingletonHolder.instance;
	}

	/**
	 * 初始化
	 */
	public boolean initProperties(Context context) {

		if (properties == null) {
			properties = new Properties();
		}

		// File f = new File(LOCAL_PATH);
		// f.delete();

		if (isExistExternalStore()) {
			String content = readContentByFile(LOCAL_PATH);
			if (content != null) {
				try {
					properties.load(new FileInputStream(LOCAL_PATH));
					loadConfigByProperties();
					return checkValidateConfig();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		// if SDcar

		try {
			properties.load(context.getResources().openRawResource(R.raw.config));
			loadConfigByProperties();
			return checkValidateConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static String Server_IP = "192.168.0.110";
	public static String Server_Port = "8080";

	public static boolean isExistExternalStore() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * /sdcard
	 *
	 * @return
	 */
	public String getExternalStorePath() {
		if (isExistExternalStore()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}

	public String readContentByFile(String path) {
		BufferedReader reader = null;
		String line = null;
		try {
			File file = new File(path);
			if (file.exists()) {
				StringBuilder sb = new StringBuilder();
				reader = new BufferedReader(new FileReader(file));
				while ((line = reader.readLine()) != null) {
					sb.append(line.trim());
				}
				return sb.toString().trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * load config info from config.properties
	 */
	private void loadConfigByProperties() {
		Server_IP = properties.getProperty("server_ip");
		Server_Port = properties.getProperty("server_port");
	}

	private boolean checkValidateConfig() {
		if (Server_IP == null || Server_Port == null) {
			return false;
		}
		return true;
	}

	public Properties get() {
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			fis = new FileInputStream(LOCAL_PATH);
			props.load(fis);
		} catch (Exception e) {
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return props;
	}

	private void setProps(Properties p) {
		FileOutputStream fos = null;
		try {

			File conf = new File(LOCAL_PATH);
			fos = new FileOutputStream(conf);

			p.store(fos, null);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取Preference设置
	 */
	public SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public String get(String key) {
		Properties props = get();
		return (props != null) ? props.getProperty(key) : null;
	}

	public void set(Properties ps) {
		Properties props = get();
		props.putAll(ps);
		setProps(props);
	}

	public void set(String key, String value) {
		Properties props = get();
		props.setProperty(key, value);
		setProps(props);
	}

	public void remove(String... key) {
		Properties props = get();
		for (String k : key)
			props.remove(k);
		setProps(props);
	}

	public void setProperties(Properties ps) {
		set(ps);
	}
}