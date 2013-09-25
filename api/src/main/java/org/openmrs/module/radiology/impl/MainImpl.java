package org.openmrs.module.radiology.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Order;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.DicomUtils;
import org.openmrs.module.radiology.Main;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.Utils;
import org.openmrs.module.radiology.Visit;
import org.openmrs.module.radiology.db.GenericDAO;
import org.openmrs.module.radiology.db.StudyDAO;
import org.openmrs.module.radiology.db.VisitDAO;
import org.springframework.transaction.annotation.Transactional;

//import com.hxti.xebra.util.XebraInterface;

public class MainImpl extends BaseOpenmrsService implements Main
{

	private GenericDAO gdao;
	private StudyDAO sdao;
	private VisitDAO vdao;
	private static final Log log=LogFactory.getLog(MainImpl.class);



	public void setSdao(StudyDAO dao)
	{
		this.sdao=dao;
	}



	public void setVdao(VisitDAO vdao)
	{
		this.vdao=vdao;
	}



	@Transactional(readOnly=true)
	public Study getStudy(Integer id)
	{
		return sdao.getStudy(id);
	}



	@Transactional(readOnly=true)
	public Study getStudyByOrderId(Integer id)
	{
		return sdao.getStudyByOrderId(id);
	}



	@Transactional
	public Study saveStudy(Study s)
	{
		return saveStudy(s,null);
	}



	public Study saveStudy(Study s,Date d)
	{
		Order order=s.order();
		try
		{
			sdao.saveStudy(s);
			File file=new File(Utils.mwlDir(),s.getId()+".xml");
			String path="";
			path=file.getCanonicalPath();
			DicomUtils.write(order,s,file);
			log.debug("Order and study saved in "+path);
			return s;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.warn("Can not save study in openmrs or dmc4che or xebra");
		}
		return null;
	}
        public void sendModalityWorklist(Study s)
        {
            Order order=s.order();
            String hl7blob=DicomUtils.createHL7Message(s,order);
            DicomUtils.sendHL7Worklist(hl7blob);
        }



	@Transactional(readOnly=true)
	public Visit getVisit(Integer id)
	{
		return vdao.getVisit(id);
	}



	@Transactional
	public Visit saveVisit(Visit v)
	{
		return vdao.saveVisit(v);
	}



	@Override
	public void setGdao(GenericDAO dao)
	{
		this.gdao=dao;
	}



	@Override
	public Object get(String query,boolean unique)
	{
		return gdao.get(query,unique);
	}
	
	public GenericDAO db(){return gdao;}

}
