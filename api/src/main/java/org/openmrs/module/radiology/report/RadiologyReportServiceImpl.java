package org.openmrs.module.radiology.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Provider;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.RadiologyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class RadiologyReportServiceImpl extends BaseOpenmrsService implements RadiologyReportService {
	
	private static final Log log = LogFactory.getLog(RadiologyReportServiceImpl.class);
	
	@Autowired
	private RadiologyReportDAO radiologyReportDAO;
	
	/**
	 * @see RadiologyReportService#createAndClaimRadiologyReport(RadiologyOrder)
	 */
	@Transactional
	@Override
	public RadiologyReport createAndClaimRadiologyReport(RadiologyOrder radiologyOrder) throws IllegalArgumentException,
			UnsupportedOperationException {
		
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null");
		}
		if (radiologyOrder.isNotCompleted()) {
			throw new IllegalArgumentException("radiologyOrder needs to be completed");
		}
		if (radiologyReportDAO.hasRadiologyOrderCompletedRadiologyReport(radiologyOrder)) {
			throw new UnsupportedOperationException(
					"cannot create radiologyReport for this radiologyOrder because it is already completed");
		}
		if (radiologyReportDAO.hasRadiologyOrderClaimedRadiologyReport(radiologyOrder)) {
			throw new UnsupportedOperationException(
					"cannot create radiologyReport for this radiologyOrder because it is already claimed");
		}
		final RadiologyReport radiologyReport = new RadiologyReport(radiologyOrder);
		return radiologyReportDAO.saveRadiologyReport(radiologyReport);
	}
	
	/**
	 * @see RadiologyReportService#saveRadiologyReport(RadiologyReport)
	 */
	@Transactional
	@Override
	public RadiologyReport saveRadiologyReport(RadiologyReport radiologyReport) throws IllegalArgumentException,
			UnsupportedOperationException {
		
		if (radiologyReport == null) {
			throw new IllegalArgumentException("radiologyReport cannot be null");
		}
		if (radiologyReport.getReportStatus() == null) {
			throw new IllegalArgumentException("radiologyReportStatus cannot be null");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.DISCONTINUED) {
			throw new UnsupportedOperationException("a discontinued radiologyReport cannot be saved");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.COMPLETED) {
			throw new UnsupportedOperationException("a completed radiologyReport cannot be saved");
		}
		return radiologyReportDAO.saveRadiologyReport(radiologyReport);
	}
	
	/**
	 * @see RadiologyReportService#unclaimRadiologyReport(RadiologyReport)
	 */
	@Transactional
	@Override
	public RadiologyReport unclaimRadiologyReport(RadiologyReport radiologyReport) throws IllegalArgumentException,
			UnsupportedOperationException {
		
		if (radiologyReport == null) {
			throw new IllegalArgumentException("radiologyReport cannot be null");
		}
		if (radiologyReport.getReportStatus() == null) {
			throw new IllegalArgumentException("radiologyReportStatus cannot be null");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.DISCONTINUED) {
			throw new UnsupportedOperationException("a discontinued radiologyReport cannot be unclaimed");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.COMPLETED) {
			throw new UnsupportedOperationException("a completed radiologyReport cannot be unclaimed");
		}
		radiologyReport.setReportStatus(RadiologyReportStatus.DISCONTINUED);
		return radiologyReportDAO.saveRadiologyReport(radiologyReport);
	}
	
	/**
	 * @see RadiologyReportService#completeRadiologyReport(RadiologyReport, Provider)
	 */
	@Transactional
	@Override
	public RadiologyReport completeRadiologyReport(RadiologyReport radiologyReport, Provider principalResultsInterpreter)
			throws IllegalArgumentException, UnsupportedOperationException {
		
		if (radiologyReport == null) {
			throw new IllegalArgumentException("radiologyReport cannot be null");
		}
		if (principalResultsInterpreter == null) {
			throw new IllegalArgumentException("principalResultsInterpreter cannot be null");
		}
		if (radiologyReport.getReportStatus() == null) {
			throw new IllegalArgumentException("radiologyReportStatus cannot be null");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.DISCONTINUED) {
			throw new UnsupportedOperationException("a discontinued radiologyReport cannot be completed");
		}
		if (radiologyReport.getReportStatus() == RadiologyReportStatus.COMPLETED) {
			throw new UnsupportedOperationException("a completed radiologyReport cannot be completed");
		}
		radiologyReport.setReportDate(new Date());
		radiologyReport.setPrincipalResultsInterpreter(principalResultsInterpreter);
		radiologyReport.setReportStatus(RadiologyReportStatus.COMPLETED);
		return radiologyReportDAO.saveRadiologyReport(radiologyReport);
	}
	
	/**
	 * @see RadiologyReportService#getRadiologyReportByRadiologyReportId(Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public RadiologyReport getRadiologyReportByRadiologyReportId(Integer radiologyReportId) throws IllegalArgumentException {
		
		if (radiologyReportId == null) {
			throw new IllegalArgumentException("radiologyReportId cannot be null");
		}
		return radiologyReportDAO.getRadiologyReportById(radiologyReportId);
	}
	
	/**
	 * @see RadiologyReportService#getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder, RadiologyReportStatus)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<RadiologyReport> getRadiologyReportsByRadiologyOrderAndReportStatus(RadiologyOrder radiologyOrder,
			RadiologyReportStatus reportStatus) throws IllegalArgumentException {
		
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null");
		}
		if (reportStatus == null) {
			throw new IllegalArgumentException("radiologyReportStatus cannot be null");
		}
		return radiologyReportDAO.getRadiologyReportsByRadiologyOrderAndRadiologyReportStatus(radiologyOrder, reportStatus)
				.size() > 0 ? radiologyReportDAO.getRadiologyReportsByRadiologyOrderAndRadiologyReportStatus(radiologyOrder,
			reportStatus) : new ArrayList<RadiologyReport>();
	}
	
	/**
	 * @see RadiologyReportService#hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder)
	 */
	@Transactional(readOnly = true)
	@Override
	public boolean hasRadiologyOrderClaimedRadiologyReport(RadiologyOrder radiologyOrder) {
		
		return radiologyOrder == null ? false : radiologyReportDAO.hasRadiologyOrderClaimedRadiologyReport(radiologyOrder);
	}
	
	/**
	 * @see RadiologyReportService#hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder)
	 */
	@Transactional(readOnly = true)
	@Override
	public boolean hasRadiologyOrderCompletedRadiologyReport(RadiologyOrder radiologyOrder) {
		
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null");
		}
		return radiologyReportDAO.hasRadiologyOrderCompletedRadiologyReport(radiologyOrder) ? true : false;
	}
	
	/**
	 * @see RadiologyReportService#getActiveRadiologyReportByRadiologyOrder(RadiologyOrder)
	 */
	@Transactional(readOnly = true)
	@Override
	public RadiologyReport getActiveRadiologyReportByRadiologyOrder(RadiologyOrder radiologyOrder) {
		
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null");
		}
		if (hasRadiologyOrderCompletedRadiologyReport(radiologyOrder)) {
			return radiologyReportDAO.getActiveRadiologyReportByRadiologyOrder(radiologyOrder);
		}
		if (hasRadiologyOrderClaimedRadiologyReport(radiologyOrder)) {
			return radiologyReportDAO.getActiveRadiologyReportByRadiologyOrder(radiologyOrder);
		}
		return null;
	}
}
