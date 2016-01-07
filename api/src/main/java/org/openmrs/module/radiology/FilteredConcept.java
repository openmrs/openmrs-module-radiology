/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p/>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;

import java.util.LinkedList;
import java.util.List;

public class FilteredConcept extends Concept {
	
	public List<String> getRadiologyConcepts() {
		List<Concept> list;
		list = Context.getConceptService().getAllConcepts();
		List<String> b = new LinkedList<String>();
		for (Concept all : list) {
			if (all.getName().getName().equals("Radiology")) {
				List<Concept> conceptSets;
				conceptSets = all.getSetMembers();
				for (Concept sets : conceptSets) {
					b.add(sets.getName().getName());
				}
			}
		}
		return b;
	}
}
