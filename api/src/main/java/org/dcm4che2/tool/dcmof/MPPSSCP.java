/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Gunter Zeilinger, Huetteldorferstr. 24/10, 1150 Vienna/Austria/Europe.
 * Portions created by the Initial Developer are Copyright (C) 2002-2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Gunter Zeilinger <gunterze@gmail.com>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4che2.tool.dcmof;

import java.io.File;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;
import org.dcm4che2.net.Association;
import org.dcm4che2.net.DicomServiceException;
import org.dcm4che2.net.Status;
import org.dcm4che2.net.service.DicomService;
import org.dcm4che2.net.service.NCreateService;
import org.dcm4che2.net.service.NSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.DicomUtils;

/**
 * @author gunter zeilinger(gunterze@gmail.com)
 * @version $Revision: 14060 $ $Date: 2010-09-30 10:29:30 +0200 (Thu, 30 Sep
 *          2010) $
 * @since Feb 2, 2006
 * 
 */
class MPPSSCP {

	private static Logger log=Logger.getLogger(MPPSSCP.class);
	private static void debug(String message){
		if(log.isDebugEnabled()) log.debug(message);
	}
	
	private final DicomService ncreatescp = new NCreateService(
			UID.ModalityPerformedProcedureStepSOPClass) {

		@Override
		protected DicomObject doNCreate(Association as, int pcid,
				DicomObject rq, DicomObject data, DicomObject rsp)
				throws DicomServiceException {
			return MPPSSCP.this.doNCreate(rq, data, rsp);
		}
	};

	private final DicomService nsetscp = new NSetService(
			UID.ModalityPerformedProcedureStepSOPClass) {

		@Override
		protected DicomObject doNSet(Association as, int pcid, DicomObject rq,
				DicomObject data, DicomObject rsp) throws DicomServiceException {
			return MPPSSCP.this.doNSet(rq, data);
		}
	};

	protected final DcmOF dcmOF;
	protected File destination;

	public MPPSSCP(DcmOF dcmOF) {
		this.dcmOF = dcmOF;
	}

	public final void setDestination(File destination) {
		destination.mkdirs();
		this.destination = destination;
	}

	public final DicomService getNCreateSCP() {
		return ncreatescp;
	}

	public final DicomService getNSetSCP() {
		return nsetscp;
	}

	private DicomObject doNCreate(DicomObject rq, DicomObject data,
			DicomObject rsp) throws DicomServiceException {
		String iuid = rq.getString(Tag.AffectedSOPInstanceUID);
		if (iuid == null)
			iuid = rsp.getString(Tag.AffectedSOPInstanceUID);
		File f = mkFile(iuid);
		if (f.exists()) {
			throw new DicomServiceException(rq, Status.DuplicateSOPinstance);
		}
		data.initFileMetaInformation(
				UID.ModalityPerformedProcedureStepSOPClass, iuid,
				UID.ExplicitVRLittleEndian);
		try {
			store(f, data);
			try {
				Context.openSession();
				DicomUtils.updateStudyPerformedStatusByMpps(data);
			} catch (Exception e) {
				debug("Can not update database with");
				debug(data.toString());
				e.printStackTrace();
			}finally{
				Context.closeSession();
			}
		} catch (Exception e) {
			throw new DicomServiceException(rq, Status.ProcessingFailure);
		}
		return null;
	}

	protected void store(File f, DicomObject data) throws Exception {
		dcmOF.storeAsDICOM(f, data);
	}

	private DicomObject doNSet(DicomObject rq, DicomObject data)
			throws DicomServiceException {
		final String iuid = rq.getString(Tag.RequestedSOPInstanceUID);
		File f = mkFile(iuid);
		if (!f.exists()) {
			throw new DicomServiceException(rq, Status.NoSuchObjectInstance,
					iuid);
		}
		try {
			DicomObject mpps = dcmOF.load(f);
			String status = mpps.getString(Tag.PerformedProcedureStepStatus);
			if (!"IN PROGRESS".equals(status)) {
				DicomServiceException ex = new DicomServiceException(rq,
						Status.ProcessingFailure,
						"Performed Procedure Step Object may no longer be updated");
				ex.setErrorID(0xA710);
				throw ex;
			}
			data.copyTo(mpps);
			store(f, mpps);
			try {
				Context.openSession();
				DicomUtils.updateStudyPerformedStatusByMpps(mpps);
			} catch (Exception e) {
				debug("Can not update database with");
				debug(mpps.toString());
				e.printStackTrace();
			}finally{
				Context.closeSession();
			}
		} catch (DicomServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new DicomServiceException(rq, Status.ProcessingFailure,
					e.getMessage());
		}
		return null;
	}

	protected File mkFile(String iuid) {
		return new File(destination, iuid);
	}

	static class XML extends MPPSSCP {

		public XML(DcmOF dcmOF) {
			super(dcmOF);
		}

		@Override
		protected File mkFile(String iuid) {
			return new File(destination, iuid + ".xml");
		}

		@Override
		protected void store(File f, DicomObject data) throws Exception {
			dcmOF.storeAsXML(f, data);
		}
	}

}
