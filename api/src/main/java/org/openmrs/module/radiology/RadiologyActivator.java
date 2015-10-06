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

	RadiologyProperties radiologyProperties;
	
	private DcmOF dicomOrderFiller;
	
	@Override
	public void started() {
		startDicomOrderFiller();
	}
	
	@Override
	public void stopped() {
		stopDicomOrderFiller();
	}
	
	public void startDicomOrderFiller() {
		try {
			radiologyProperties = Context.getRegisteredComponent("radiologyProperties", RadiologyProperties.class);
			String[] args2 = { "-mwl", radiologyProperties.getMwlDir(), "-mpps", radiologyProperties.getMppsDir(),
			        radiologyProperties.getApplicationEntityTitle() + ":" + radiologyProperties.getMwlMppsPort() };
			dicomOrderFiller = DcmOF.main(args2);
			log.info("Started MPPSScu : OpenMRS MPPS SCU Client (dcmof)");
		}
		catch (Exception e) {
			log.warn("Can not start MWL/MPPS DICOM server");
			log.warn("Unable to start MPPSScu : OpenMRS MPPS SCU Client (dcmof)");
		}
	}
	
	public void stopDicomOrderFiller() {
		dicomOrderFiller.stop();
		log.info("Stopped MPPSScu : OpenMRS MPPS SCU Client (dcmof)");
	}
}
