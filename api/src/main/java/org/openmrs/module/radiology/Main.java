package org.openmrs.module.radiology;

import java.util.Date;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
import org.openmrs.module.radiology.db.GenericDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.openmrs.module.radiology.db.VisitDAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Main extends OpenmrsService {

	public void setGdao(GenericDAO dao);
	public void setSdao(StudyDAO dao);
	public void setVdao(VisitDAO dao);
	
	public Object get(String query,boolean unique);

	public Study getStudy(Integer id);

	public Study saveStudy(Study os);
	public Study saveStudy(Study os,Date d);
        public void sendModalityWorklist(Study s,OrderRequest orderRequest);
	
	public Visit getVisit(Integer id);

	public Visit saveVisit(Visit v);
	
	public Study getStudyByOrderId(Integer id);
	
	public GenericDAO db();
	
}