package org.openmrs.module.radiology.hl7;

import ca.uhn.hl7v2.parser.EncodingCharacters;

/**
 * Contains constants used by HL7 classes.
 */
public class HL7Constants {
	
	public static final EncodingCharacters ENCODING_CHARACTERS = new EncodingCharacters('|', '^', '~', '\\', '&');
	
}
