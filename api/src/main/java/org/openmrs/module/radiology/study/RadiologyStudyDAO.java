/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.study;

/**
 * {@code RadiologyStudy} related database methods.
 * 
 * @see org.openmrs.module.radiology.study.RadiologyStudyService
 * @see org.openmrs.module.radiology.study.RadiologyStudy
 */
interface RadiologyStudyDAO {
    
    
    /**
     * @see org.openmrs.module.radiology.study.RadiologyStudyService#saveRadiologyStudy(RadiologyStudy)
     */
    public RadiologyStudy saveRadiologyStudy(RadiologyStudy radiologyStudy);
    
    /**
     * @see org.openmrs.module.radiology.study.RadiologyStudyService#getRadiologyStudy(Integer)
     */
    public RadiologyStudy getRadiologyStudy(Integer studyId);
    
    /**
     * @see org.openmrs.module.radiology.study.RadiologyStudyService#getRadiologyStudyByUuid(String)
     */
    public RadiologyStudy getRadiologyStudyByUuid(String uuid);
    
    /**
     * @see org.openmrs.module.radiology.study.RadiologyStudyService#getRadiologyStudyByStudyInstanceUid(String)
     */
    public RadiologyStudy getRadiologyStudyByStudyInstanceUid(String studyInstanceUid);
}
