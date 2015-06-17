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

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dcm4che2.tool.dcmof.DcmOF;
import org.openmrs.OrderType;
import org.openmrs.Role;
import org.openmrs.api.OrderService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */

public class RadiologyActivator extends BaseModuleActivator {
	
	private static final Log log = LogFactory.getLog(RadiologyActivator.class);
	
	private DcmOF dicomOrderFiller;
	
	@Override
	public void started() {
		start();
	}
	
	@Override
	public void stopped() {
		stopDicomOrderFiller();
	}
	
	public static boolean badInit(UserService us, OrderService os) {
		return !Roles.created(us) || !Utils.hasRadiology(os);
	}
	
	public void start() {
		try {
			startDicomOrderFiller();
		}
		catch (Exception e) {
			// Just prints in console
		}
		typeAndRoles();
	}
	
	/**
	 * Creates radiology order type if not exists Creates "Scheduler", "Referring Physician",
	 * "Performing Physician", "Reading Physician" roles
	 */
	public static boolean typeAndRoles() {
		// Create radiology order type if not exists
		try {
			if (Utils.getRadiologyOrderType().isEmpty()) {
				OrderService os = Context.getOrderService();
				os.saveOrderType(new OrderType("Radiology", "Order for radiology procedures"));
			}
			log.info("Radiology order type created!");
		}
		catch (Exception e) {
			log
			        .warn("Need some privilege to startup the module. Go to openmrs/module/radiology/config.list with authenticated user.");
			return false;
		}
		
		try {
			// Create "Scheduler", "Referring Physician",
			// "Performing Physician", "Reading Physician" roles
			Field[] roles = Roles.class.getDeclaredFields();
			for (Field role : roles) {
				String sRole = Roles.value(role);
				Role toSave = new Role(sRole, sRole);
				toSave.setCreator(Context.getUserService().getUser(1));
				
				// Check if role has already been added.
				List<Role> allRoles = Context.getUserService().getAllRoles();
				boolean rolePresent = false;
				for (Role eachRole : allRoles) {
					if (eachRole.getRole().equals(toSave.getRole())) {
						rolePresent = true;
					}
				}
				if (rolePresent) {
					continue;
				}
				
				HashSet<Role> set = new HashSet<Role>();
				if (Utils.devMode()) {
					// Inherits privileges from System developer
					Role parent = Context.getUserService().getRole("System Developer");
					set.add(parent);
					toSave.setInheritedRoles(set);
				} else {
					// TODO set correct privileges...
					Role parent = Context.getUserService().getRole("Provider");
					if (parent.getPrivileges().isEmpty())
						parent = Context.getUserService().getRole("System Developer");
					set.add(parent);
					toSave.setInheritedRoles(set);
				}
				Context.getUserService().saveRole(toSave);
			}
			log.info("\"Scheduler\", \"Referring Physician\",\"Performing Physician\", \"Reading Physician\" Roles created");
		}
		catch (Exception e) {
			log.warn("Can not create \"Scheduler\", \"Referring Physician\", \"Performing Physician\", \"Reading Physician\" roles. Go to openmrs/module/radiology/config.list with authenticated user.");
			return false;
		}
		return true;
	}
	
	public void startDicomOrderFiller() throws Exception {
		try {
			String[] args2 = { "-mwl", Utils.mwlDir(), "-mpps", Utils.mppsDir(), Utils.aeTitle() + ":" + Utils.mwlMppsPort() };
			dicomOrderFiller = DcmOF.main(args2);
			log.info("Started MPPSScu : OpenMRS MPPS SCU Client (dcmof)");
		}
		catch (Exception e) {
			log.warn("Can not start MWL/MPPS DICOM server");
			log.warn("Unable to start MPPSScu : OpenMRS MPPS SCU Client (dcmof)");
			throw e;
		}
	}
	
	public void stopDicomOrderFiller() {
		dicomOrderFiller.stop();
		log.info("Stopped MPPSScu : OpenMRS MPPS SCU Client (dcmof)");
	}
}
