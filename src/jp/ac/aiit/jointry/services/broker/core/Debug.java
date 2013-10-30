package jp.ac.aiit.jointry.services.broker.core;
import java.util.LinkedList;

public class Debug {

	static private class StackZone {
		private String[] brokerPackages = {
			"broker", "app",
		};
		private char type;		// 'A': アプリ, 'L': ライブラリ
		int index;				// StackTraceElement[] の開始位置
		int count = 1;			// 要素数（生成時に１行登録される）

		StackZone(String fqcn, int index) {
			type = getType(fqcn);
			this.index = index;
		}
		int lastIndex() { return index + count - 1; }
		boolean typeChanged(String fqcn) { return getType(fqcn) != type; }
		boolean isApplicationCall() { return type == 'A'; }
		private char getType(String fqcn) {
			for(String bp : brokerPackages)
				if(fqcn.startsWith(bp)) return 'A';
			return 'L';
		}
	}

	static public void printStackTrace(Throwable t, Monitor monitor) {
		monitor.println("" + t);
		StackTraceElement[] stes = t.getStackTrace();
		LinkedList<StackZone> zones = new LinkedList<StackZone>();
		StackZone currentZone = null;
		int index = 0;
		for(StackTraceElement st : stes) {
			String fqcn = st.getClassName();
			if(currentZone == null || currentZone.typeChanged(fqcn)) {
				// 最初のZoneか型が変わったら新しくZoneを生成しzonesに登録する。
				currentZone = new StackZone(fqcn, index);
				zones.add(currentZone);
			} else {
				currentZone.count++;
			}
			index++;
		}
		while(!zones.isEmpty()) {
			StackZone z = zones.removeLast();
			if(z.isApplicationCall() || z.count <= 3) {
				for(int i = z.lastIndex(); i >= z.index; i--)
					monitor.println("\t" + traceStatement(stes[i]));
			} else {
				monitor.println("\t" + traceStatement(stes[z.lastIndex()]));
				monitor.println("\t... " + (z.count-2) +
								" library methods called.");
				monitor.println("\t" + traceStatement(stes[z.index]));
			}
			monitor.println("\t----------------------------------------");
		}
	}

	static private String traceStatement(StackTraceElement st) {
		String fname = st.getFileName();
		String detail = "Unknown Source";
		if(fname != null) {
			detail = fname + ":" + st.getLineNumber();
		} else if(st.isNativeMethod()) {
			detail = "Native Method";
		}
		return st.getClassName() + "." + st.getMethodName() + "("+detail+")";
	}

	static public boolean probeStack(Monitor monitor) {
		try { throw new Exception(); } catch(Exception e) {
			printStackTrace(e, monitor);
		}
		return true;
	}
}
