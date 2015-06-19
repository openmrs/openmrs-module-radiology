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
import org.openmrs.module.radiology.MwlStatus;
import org.openmrs.module.radiology.RadiologyService;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.Utils;
import org.openmrs.module.radiology.db.GenericDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.springframework.transaction.annotation.Transactional;

public class RadiologyServiceImpl extends BaseOpenmrsService implements RadiologyService {
	
	private GenericDAO gdao;
	
	private StudyDAO sdao;
	
	private static final Log log = LogFactory.getLog(RadiologyServiceImpl.class);
	
	@Override
	public void setSdao(StudyDAO dao) {
		this.sdao = dao;
	}
	
	@Transactional(readOnly = true)
	@Override
	public Study getStudy(Integer id) {
		return sdao.getStudy(id);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Study getStudyByOrderId(Integer id) {
		return sdao.getStudyByOrderId(id);
	}
	
	@Transactional
	@Override
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
			log.error(e.getMessage(), e);
			log.warn("Can not save study in openmrs or dmc4che.");
		}
		return null;
	}
	
	@Override
	public void sendModalityWorklist(Study s, OrderRequest orderRequest) {
		Order order = s.order();
		MwlStatus mwlStatus = s.getMwlStatus();
		String hl7blob = DicomUtils.createHL7Message(s, order, orderRequest);
		int status = DicomUtils.sendHL7Worklist(hl7blob);
		
		if (status == 1) {
			switch (orderRequest) {
				case Save_Order:
					if (mwlStatus == MwlStatus.DEFAULT || mwlStatus == MwlStatus.SAVE_ERR) {
						mwlStatus = MwlStatus.SAVE_OK;
					} else {
						mwlStatus = MwlStatus.UPDATE_OK;
					}
					break;
				case Void_Order:
					mwlStatus = MwlStatus.VOID_OK;
					break;
				case Unvoid_Order:
					mwlStatus = MwlStatus.UNVOID_OK;
					break;
				case Discontinue_Order:
					mwlStatus = MwlStatus.DISCONTINUE_OK;
					break;
				case Undiscontinue_Order:
					mwlStatus = MwlStatus.UNDISCONTINUE_OK;
					break;
				default:
					break;
				
			}
			
		} else if (status == 0) {
			switch (orderRequest) {
				case Save_Order:
					if (mwlStatus == MwlStatus.DEFAULT || mwlStatus == MwlStatus.SAVE_ERR) {
						mwlStatus = MwlStatus.SAVE_ERR;
					} else {
						mwlStatus = MwlStatus.UPDATE_ERR;
					}
					break;
				case Void_Order:
					mwlStatus = MwlStatus.VOID_ERR;
					break;
				case Unvoid_Order:
					mwlStatus = MwlStatus.UNVOID_ERR;
					break;
				case Discontinue_Order:
					mwlStatus = MwlStatus.DISCONTINUE_ERR;
					break;
				case Undiscontinue_Order:
					mwlStatus = MwlStatus.UNDISCONTINUE_ERR;
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
	
	@Override
	public GenericDAO db() {
		return gdao;
	}
	
}
