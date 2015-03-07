package org.openmrs.module.radiology.db;

import org.openmrs.module.radiology.Visit;

public interface VisitDAO {
	
	public Visit getVisit(Integer id);
	
	public Visit saveVisit(Visit v);
	
}
