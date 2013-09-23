package org.dcm4che2.tool.dcmrcv;

import java.io.File;
import java.util.concurrent.Executor;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.DimseRSP;
import org.dcm4che2.net.service.CFindService;

public class CFindSCP extends CFindService {

	private final DcmRcv dcmrcv;
	protected File source;

	public CFindSCP(String sopClass, Executor executor, DcmRcv dcmrcv) {
		super(sopClass, executor);
		this.dcmrcv = dcmrcv;
	}

	public CFindSCP(String[] sopClasses, Executor executor, DcmRcv dcmrcv) {
		super(sopClasses, executor);
		this.dcmrcv = dcmrcv;
	}
	
	public final void setSource(File source) {
        source.mkdirs();
        this.source = source;
    }

	@Override
	protected DimseRSP doCFind(Association as, int pcid, DicomObject cmd,
			DicomObject queryObj, DicomObject rsp) throws DicomServiceException {
		return new MultiFindRSP(dcmrcv, queryObj, rsp, source);
	}
}
