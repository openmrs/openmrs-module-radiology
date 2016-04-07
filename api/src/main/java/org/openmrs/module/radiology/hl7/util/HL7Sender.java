package org.openmrs.module.radiology.hl7.util;

import org.dcm4che.tool.hl7snd.HL7Snd;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.RadiologyProperties;

public class HL7Sender {
	
	private static final int HL7_SEND_SUCCESS = 1;
	
	private static RadiologyProperties radiologyProperties = Context.getRegisteredComponent("radiologyProperties",
		RadiologyProperties.class);
	
	// Send HL7 ORU message to dcm4chee.
	public static boolean sendHL7Message(String hl7message) {
		final String input[] = { "-c", radiologyProperties.getPacsAddress() + ":" + radiologyProperties.getPacsHL7Port(),
				hl7message };
		final int hl7SendStatus = HL7Snd.main(input);
		return hl7SendStatus == HL7_SEND_SUCCESS ? true : false;
	}
}
