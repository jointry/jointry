package jp.ac.aiit.jointry.services.broker.core;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jp.ac.aiit.jointry.services.broker.util.Util;

/**
 * ユーザアカウントの管理:<br>
 * ユーザのIDとPASSWORDを管理する。
 */
public class Account implements Common {
	final static String ACCOUNT_FILE = "account.txt";
	final static int NULL_PROXYID = 0; // < 1

	private static String accountFile = ACCOUNT_FILE;

	public static void setAccountFile(String fname) {
		accountFile = fname;
	}

	/**
	 * Singletonパターン
	 */
	public static Account getInstance() {
		if(acc == null) acc = new Account();
		return acc;
	}

	private static Account acc;

	private static String[][] defaultMembers = {
		{ "Takashi", "yt" },
		{ "Matsuko", "ym" },
		{ "Noboru",  "yb" },
		{ "Nonoko",  "yn" },
		{ "Shige",   "ys" },
	};

	public void login(String name) {
		if(name == null) return;
		UserInfo u = mapAcc.get(name);
		if(u != null) u.increment();
	}

	public void logout(String name) {
		UserInfo u = mapAcc.get(name);
		if(u != null) u.proxyID = 0;
	}

	public void addActiveUser(String name, int proxyID) {
		UserInfo u = mapAcc.get(name);
		if(u != null) u.proxyID = proxyID;
	}

	/**
	 * ユーザ情報
	 */
	public static class UserInfo {
		String name;			// ユーザ名
		String password;		// パスワード
		int count;				// 接続回数
		int limit;				// 接続回数上限（0の場合は無制限）
		int proxyID = NULL_PROXYID;

		public String password() { return password; }
		public void password(String password) { this.password = password; }

		public UserInfo(String name, String password, int count, int limit) {
			this.name = name;
			this.password = password;
			this.count = count;
			this.limit = limit;
		}
		boolean certify(String pw) {
			return password.equals(pw) && (limit == 0 || count < limit);
		}
		void increment() { count += 1; }
	}

	private Map<String,UserInfo> mapAcc = new TreeMap<String,UserInfo>();

	protected Account() {
		List<String[]> list = Util.readArray(accountFile);
		if(list == null) {
			for(String[] p : defaultMembers) add(p[0], p[1], 0, 0);
			return;
		}
		for(String[] a : list) {
			switch(a.length) {
			case 3: add(a[0], a[1], Util.getInt(a[2]), 0); break;
			case 4: add(a[0], a[1], Util.getInt(a[2]), Util.getInt(a[3])); break;
			default:
				System.err.println("Account: Illegal data: " + Util.toString(a));
				break;
			}
		}
	}

	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("*** Account List\n");
		sb.append("------------------------------------------------------\n");
		sb.append("    name         password       count   limit  proxyID\n");
		sb.append("======================================================\n");
		int i = 0;
		for(UserInfo user : mapAcc.values()) {
			String pid = (user.proxyID != NULL_PROXYID) ? ""+user.proxyID : "";
			sb.append(String.format("%2d. %-12s %-12s %7d %7d %8s\n", ++i,
									user.name, user.password,
									user.count, user.limit, pid));
		}
		sb.append("------------------------------------------------------\n");
		return sb.toString();
	}

	public void save() {
		StringBuilder sb = new StringBuilder();
		for(UserInfo user : mapAcc.values()) {
			sb.append(user.name + "\t" + user.password + "\t" +
					  user.count + "\t" + user.limit + "\n");
		}
		Util.writeText(accountFile, sb.toString());
	}

	public UserInfo getUserInfo(String name) { return mapAcc.get(name); }

	public void add(String name, String password, int count, int limit) {
		mapAcc.put(name, new UserInfo(name, password, count, limit));
	}

	public void remove(String name) {
		mapAcc.remove(name);
	}

	public boolean isActiveUser(String name) {
		UserInfo u = mapAcc.get(name);
		return (u != null) && u.proxyID != NULL_PROXYID;
	}

	public boolean certify(URLInfo u) {
		return certify(u.getQuery(USER_ID), u.getQuery(PASSWORD));
	}

	public boolean certify(String name, String password) {
		UserInfo u = mapAcc.get(name);
		return (u != null) && u.certify(password);
	}
}
