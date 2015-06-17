package test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dcm4che2.tool.dcmof.DcmOF;

public class Scratch {

	long x;
	private static final Log log = LogFactory.getLog(Scratch.class);

	public static Scratch s;

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException,
			IOException {
		File file=new File("/tmp");
		System.out.println(file.getCanonicalPath());
	}

	static void servers() {
		String[] a = { "-mwl", ".", "AE:11113" };
		print("1 ");
		DcmOF sh = DcmOF.main(a);

		print("2 ");

		sh.stop();
		print("3 ");
	}

	private static void print(String id) {
		try {
			log.info(id);
		} catch (Throwable t) {
			log.error("err " + t);
		}
	}

	public static String[] forSelect(Class<?> c) {
		Field[] f = c.getDeclaredFields();
		String str[] = new String[f.length];
		for (int i = 0; i < f.length; i++) {
			str[i] = f[i].getName();
		}
		return str;
	}

}
