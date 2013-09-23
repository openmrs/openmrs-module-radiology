package test;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.SpecificCharacterSet;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.ContentHandlerAdapter;
import org.dcm4che2.io.DicomInputStream;

public class DicomObjectGet
{

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		DicomObject o=new DicomObjectGet().load(new File("/eclipse/mwl/4.xml"));
		String value=o.get(new int[]{Tag.RequestedProcedureCodeSequence,Tag.CodeMeaning}).getValueAsString(new SpecificCharacterSet("UTF-8"),0);
		System.out.println();
	}

	
	DicomObject load(File f) throws Exception {
      System.out.println("M-READ " + f);
      return f.getName().endsWith(".xml") ? loadXML(f) : loadDICOM(f);
  }

  private DicomObject loadDICOM(File f) throws Exception {
      DicomInputStream in = new DicomInputStream(f);
      try {
          return in.readDicomObject();
      } finally {
          in.close();
      }
  }

  private DicomObject loadXML(File f) throws Exception {
      DicomObject dcmobj = new BasicDicomObject();
      SAXParser p = SAXParserFactory.newInstance().newSAXParser();
      ContentHandlerAdapter ch = new ContentHandlerAdapter(dcmobj);
      p.parse(f, ch);
      return dcmobj;
  }
}
