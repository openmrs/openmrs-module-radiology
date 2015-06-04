/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.impl;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Order;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.DicomUtils;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.Main;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.Utils;
import org.openmrs.module.radiology.db.GenericDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.springframework.transaction.annotation.Transactional;

public class MainImpl extends BaseOpenmrsService implements Main {
	
	private GenericDAO gdao;
	
	private StudyDAO sdao;
	
	private static final Log log = LogFactory.getLog(MainImpl.class);
	
	public void setSdao(StudyDAO dao) {
		this.sdao = dao;
	}
	
	@Transactional(readOnly = true)
	public Study getStudy(Integer id) {
		return sdao.getStudy(id);
	}
	
	@Transactional(readOnly = true)
	public Study getStudyByOrderId(Integer id) {
		return sdao.getStudyByOrderId(id);
	}
	
	@Transactional
	public Study saveStudy(Study s) {
		
		Order order = s.order();
		try {
			sdao.saveStudy(s);
			File file = new File(Utils.mwlDir(), s.getId() + ".xml");
			String path = "";
			path = file.getCanonicalPath();
			DicomUtils.write(order, s, file);
			log.debug("Order and study saved in " + path);
			return s;
		}
		catch (Exception e) {
			e.printStackTrace();
			log.warn("Can not save study in openmrs or dmc4che.");
		}
		return null;
	}
	
	//MWL Status Codes, these are custom codes to help determine what sync status of the order is.
	// -1 :default
	// 0 : save order successful
	// 1 : save order failed . Save Again
	// 2 : Update order succesful .
	// 3 : Update order failed. Save again.
	// 4 : Void order succesful .
	// 5 : Void order failed. Try again.
	// 6 : Discontinue order succesful .
	// 7 : Discontinue order failed. Try again.
	// 8 : Undiscontinue order succesful .
	// 9 : Undiscontinue order failed. Try again.
	// 10 : Unvoid order successfull
	// 11 : Unvoid order failed. Try again
	public void sendModalityWorklist(Study s, OrderRequest orderRequest) {
		Order order = s.order();
		Integer mwlStatus = s.getMwlStatus();
		String hl7blob = DicomUtils.createHL7Message(s, order, orderRequest);
		int status = DicomUtils.sendHL7Worklist(hl7blob);
		
		if (status == 1) {
			switch (orderRequest) {
				case Save_Order:
					if (mwlStatus.intValue() == 0 || mwlStatus.intValue() == 2)
						mwlStatus = 1;
					else
						mwlStatus = 3;
					break;
				case Void_Order:
					mwlStatus = 5;
					break;
				case Unvoid_Order:
					mwlStatus = 11;
					break;
				case Discontinue_Order:
					mwlStatus = 7;
					break;
				case Undiscontinue_Order:
					mwlStatus = 9;
					break;
				case Default:
					mwlStatus = 0;
					break;
				default:
					break;
				
			}
			
		} else if (status == 0) {
			switch (orderRequest) {
				case Save_Order:
					if (mwlStatus.intValue() == 0 || mwlStatus.intValue() == 2)
						mwlStatus = 2;
					else
						mwlStatus = 4;
					break;
				case Void_Order:
					mwlStatus = 6;
					break;
				case Unvoid_Order:
					mwlStatus = 12;
					break;
				case Discontinue_Order:
					mwlStatus = 8;
					break;
				case Undiscontinue_Order:
					mwlStatus = 10;
					break;
				case Default:
					mwlStatus = 0;
					break;
				default:
					break;
			}
		}
		s.setMwlStatus(mwlStatus);
		saveStudy(s);
	}
	
	@Override
	public void setGdao(GenericDAO dao) {
		this.gdao = dao;
	}
	
	@Override
	public Object get(String query, boolean unique) {
		return gdao.get(query, unique);
	}
	
	public GenericDAO db() {
		return gdao;
	}
	
}
