/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.radiology;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dcm4che2.tool.dcmof.DcmOF;
import org.dcm4che2.tool.dcmrcv.DcmRcv;
import org.openmrs.OrderType;
import org.openmrs.Role;
import org.openmrs.api.OrderService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;

//import com.hxti.xebra.util.XebraInterface;

/**
 * This class contains the logic that is run every time this module is either
 * started or shutdown
 */

public class Activator extends BaseModuleActivator {
	
	private static final Log log = LogFactory.getLog(Activator.class);
	
	@Override
	public void started() {
		start();
	}
	
	@Override
	public void stopped() {
		//   Server.dcmof.stop();
	}
	
	public static boolean badInit(UserService us, OrderService os) {
		return !Roles.created(us) || !Utils.hasRadiology(os);
	}
	
	public static void start() {
		try {
			orderFiller();
			//	storageServer();
		}
		catch (Exception e) {
			// Just prints in console
		}
		typeAndRoles();
		//		createScp();
		//		createAE();
	}
	
	/**
	 * Creates radiology order type if not exists Creates "Scheduler",
	 * "Referring Physician", "Performing Physician", "Reading Physician" roles
	 */
	public static boolean typeAndRoles() {
		// Create radiology order type if not exists
		try {
			if (Utils.getRadiologyOrderType().size() == 0) {
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
					if (parent.getPrivileges().size() == 0)
						parent = Context.getUserService().getRole("System Developer");
					set.add(parent);
					toSave.setInheritedRoles(set);
				}
				Context.getUserService().saveRole(toSave);
			}
			log.info("\"Scheduler\", \"Referring Physician\",\"Performing Physician\", \"Reading Physician\" Roles created");
		}
		catch (Exception e) {
			log
			        .warn("Can not create \"Scheduler\", \"Referring Physician\", \"Performing Physician\", \"Reading Physician\" roles. Go to openmrs/module/radiology/config.list with authenticated user.");
			return false;
		}
		return true;
	}
	
	public static void orderFiller() throws Exception {
		try {
			String[] args2 = { "-mwl", Utils.mwlDir(), "-mpps", Utils.mppsDir(), Utils.aeTitle() + ":" + Utils.mwlMppsPort() };
			DcmOF.main(args2);
			log.info("Started MPPSScu : OpenMRS MPPS SCU Client (dcmof)");
		}
		catch (Exception e) {
			e.printStackTrace();
			log.warn("Can not start MWL/MPPS DICOM server");
			log.warn("Unable to start MPPSScu : OpenMRS MPPS SCU Client (dcmof)");
			throw e;
		}
	}
	
	// Code from the Old Radiology Module which uses Xebra PACS Client and stores images locally using dcmrcv        
	//	public static void storageServer() throws Exception {
	//		try {
	//			String[] args3 = { "-dest", Utils.storageDir(),
	//					Utils.aeTitle() + ":" + Utils.storagePort(), "-scport", Utils.storageCommitmentPort()};
	//			DcmRcv.main(args3);
	//			String saving = "\nSaving:\n  mpps entries in "
	//					+ new File(Utils.mppsDir()).getAbsolutePath()
	//					+ "\n  mwl entries in "
	//					+ new File(Utils.mwlDir()).getAbsolutePath()
	//					+ "\n  DICOM objects in "
	//					+ new File(Utils.storageDir()).getAbsolutePath();
	//			log.info(saving);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//			log.warn("Can not start storage DICOM server");
	//			throw e;
	//		}
	//	}
	
	//	public static boolean createAE()
	//   {
	//	   int storagePort=Integer.parseInt(Utils.storagePort());
	//		try
	//      {
	////	      new XebraInterface().saveAE(Utils.serversAddress(),storagePort,Utils.aeTitle());
	//	      log.info("AE peer created succesfully!");
	//      }
	//      catch(Exception e)
	//      {
	//	      log.error("Can not save AE Peer");
	//	      return false;
	//      }
	//		return true;
	//   }
	//
	//	public static boolean createScp()
	//   {
	//	   String storageDir=Utils.storageDir();
	//	   String tmpDirectory=StringUtils.path(storageDir,"tmp");
	//		int storagePort=Integer.parseInt(Utils.storagePort());
	//		try
	//      {
	//	//      new XebraInterface().saveSCP(Utils.aeTitle(),storageDir,storagePort,tmpDirectory);
	//	      log.info("SCP created succesfully!");
	//      }
	//      catch(Exception e)
	//      {
	//      	log.error("Can not save SCP");
	//      	return false;
	//      }
	//		return true;
	//   }
	//
	
}
