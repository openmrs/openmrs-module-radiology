/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dcm4che2.tool.dcmof.DcmOF;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */

public class RadiologyActivator extends BaseModuleActivator {
	
	private static final Log log = LogFactory.getLog(RadiologyActivator.class);
	
	private DcmOF dicomOrderFiller;
	
	@Override
	public void willStart() {
		log.info("Trying to start up Radiology Module");
	}
	
	@Override
	public void started() {
		startDicomOrderFiller();
		log.info("Radiology Module successfully started");
	}
	
	@Override
	public void willStop() {
		log.info("Trying to shut down Radiology Module");
	}
	
	@Override
	public void stopped() {
		stopDicomOrderFiller();
		log.info("Radiology Module successfully stopped");
	}
	
	/**
	 * Start dicom order filler
	 * 
	 * @should successfully start the dicom order filler
	 */
	void startDicomOrderFiller() {
		final String[] dicomOrderFillerArguments = getDicomOrderFillerArguments();
		log.info("Trying to start OpenMRS MPPS SCU Client (dcmof) with: " + Arrays.asList(dicomOrderFillerArguments));
		dicomOrderFiller = DcmOF.main(dicomOrderFillerArguments);
	}
	
	/**
	 * Return dicom order filler arguments
	 * 
	 * @return dicom order filler arguments
	 * @should return dicom order filler arguments
	 */
	String[] getDicomOrderFillerArguments() {
		log.info("Loading dicom order filler arguments");
		final RadiologyProperties radiologyProperties = Context.getRegisteredComponent("radiologyProperties",
			RadiologyProperties.class);
		return new String[] { "-mwl", radiologyProperties.getMwlDir(), "-mpps", radiologyProperties.getMppsDir(),
				radiologyProperties.getDicomAeTitle() + ":" + radiologyProperties.getDicomMppsPort() };
	}
	
	/**
	 * Stop dicom order filler
	 * 
	 * @should throw exception when unable to stop the dicom order filler
	 * @should successfully stop the dicom order filler
	 */
	void stopDicomOrderFiller() {
		log.info("Trying to stop MPPSScu : OpenMRS MPPS SCU Client (dcmof)");
		dicomOrderFiller.stop();
	}
}
