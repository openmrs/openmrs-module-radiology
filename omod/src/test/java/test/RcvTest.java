package test;

import java.io.IOException;

import org.dcm4che2.tool.dcmrcv.DcmRcv;

public class RcvTest {

	public static void main(String[] args) throws IOException,
			InterruptedException {
//		BasicConfigurator.configure();
		for (String arg : args) {
			System.out.println(arg);
		}

		String[] args1 = { "RADIOLOGYMODULE@localhost:11112", "-dest", "\\tmp\\rcv" };
		DcmRcv rcv = DcmRcv.main(args1);

	}

}
