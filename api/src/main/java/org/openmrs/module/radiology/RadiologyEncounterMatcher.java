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

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.module.emrapi.encounter.EncounterParameters;
import org.openmrs.module.emrapi.encounter.matcher.BaseEncounterMatcher;
import org.springframework.stereotype.Component;

/**
 * Find {@link org.openmrs.Encounter} from a {@link org.openmrs.Visit}.
 */
@Component
public class RadiologyEncounterMatcher implements BaseEncounterMatcher {
	
	/**
	 * Returns existing encounter according to visit and encounter parameters
	 * 
	 * @param visit containing encounters to me matched
	 * @param encounterParameters encounter parameters of the encounter to be matched
	 * @return existing encounter according to visit and encounter parameters
	 * @throws IllegalArgumentException if visit is null
	 * @throws IllegalArgumentException if encounterParameters are null
	 * @should return encounter if encounter uuid given by encounter parameters is attached to given visit and is not voided
	 * @should return null given visit without non voided encounters
	 * @should return null if encounter uuid given by encounter parameters is voided
	 * @should throw illegal argument exception if given visit is null
	 * @should throw illegal argument exception if given encounterParameters are null
	 */
	@Override
	public Encounter findEncounter(Visit visit, EncounterParameters encounterParameters) {
		if (visit == null) {
			throw new IllegalArgumentException("visit is required");
		}
		
		if (encounterParameters == null) {
			throw new IllegalArgumentException("encounterParameters are required");
		}
		
		for (final Encounter encounter : visit.getNonVoidedEncounters()) {
			if (encounter.getUuid()
					.equals(encounterParameters.getEncounterUuid())) {
				return encounter;
			}
		}
		return null;
	}
}
