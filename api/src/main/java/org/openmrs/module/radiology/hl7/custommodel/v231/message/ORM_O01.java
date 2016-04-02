/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.custommodel.v231.message;

import org.openmrs.module.radiology.hl7.custommodel.v231.segment.ZDS;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;

/**
 * <p>
 * Represents a custom ORM_O01 message structure including the custom ZDS segment defined by IHE Radiology Technical
 * Framework Volume 2 (Rev 13.0). This structure contains the following elements:
 * </p>
 * <ul>
 * <li>1-4: segments defined by ORM_O01</li>
 * <li>5: custom ZDS segment</li>
 * </ul>
 */
public class ORM_O01 extends ca.uhn.hl7v2.model.v231.message.ORM_O01 {
	
	private static final long serialVersionUID = -7905942224054931000L;
	
	/**
	 * Creates a new ORM_O01 message with given factory.
	 * 
	 * @param factory ModelClassFactory
	 * @throws HL7Exception
	 */
	public ORM_O01(ModelClassFactory factory) throws HL7Exception {
		super(factory);
		init();
	}
	
	/**
	 * Creates a new ORM_O01 message
	 * 
	 * @throws HL7Exception
	 * @should create a new ORM_O01 instance
	 */
	public ORM_O01() throws HL7Exception {
		super(new DefaultModelClassFactory());
		init();
	}
	
	private void init() throws HL7Exception {
		final boolean required = true;
		final boolean repeating = false;
		this.add(ZDS.class, required, repeating);
	}
	
	/**
	 * Accessor for the ZDS segment
	 * 
	 * @return ZDS the ZDS segment
	 * @throws HL7Exception
	 * @should return the ZDS segment
	 */
	public ZDS getZDS() throws HL7Exception {
		ZDS result = null;
		result = (ZDS) get("ZDS");
		return result;
	}
}
