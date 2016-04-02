/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.custommodel.v231.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.v231.datatype.RP;
import ca.uhn.hl7v2.parser.ModelClassFactory;

/**
 * <p>
 * Represents a custom HL7 ZDS message segment as defined in IHE Radiology Technical Framework Volume 2 (Rev 13.0). Note that
 * custom segments extend {@link AbstractSegment}. This segment contains following fields:
 * </p>
 * <ul>
 * <li>ZDS-1: Study Instance UID (RP)
 * </ul>
 */
public class ZDS extends AbstractSegment {
	
	private static final long serialVersionUID = 3446425375733818865L;
	
	/**
	 * Creates a new ZDS segment
	 * 
	 * @param parent parent group
	 * @param factory ModelClassFactory
	 * @throws HL7Exception
	 * @should create a new ZDS instance given all params
	 */
	public ZDS(Group parent, ModelClassFactory factory) throws HL7Exception {
		super(parent, factory);
		init();
	}
	
	private void init() throws HL7Exception {
		
		// Study Instance UID Field (DataType RP - reference pointer)
		final boolean segmentIsRequired = true;
		int segmentMaxRepetitions = 0;
		int segmentLength = 200;
		this.add(RP.class, segmentIsRequired, segmentMaxRepetitions, segmentLength, new Object[] { getMessage() });
	}
	
	/**
	 * Returns ZDS-1: "Study Instance UID" - creates it if necessary
	 * 
	 * @should return study instance uid field
	 */
	public RP getStudyInstanceUID() throws HL7Exception {
		RP result = null;
		result = (RP) getField(1, 0);
		return result;
	}
	
}
