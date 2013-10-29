package org.openmrs.module.radiology.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Order;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.radiology.DicomUtils;
import org.openmrs.module.radiology.DicomUtils.OrderRequest;
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
			log.warn("Can not save study in openmrs or dmc4che.");
		}
		return null;
	}
        
        //MWL Status Codes, these are custom codes to help determine what sync status of the order is.
        // -1 :default
        // 0 : save order successful
        // 1 : save order failed . Save Again
        // 2 : Update order succesful .
        // 3 : Update order failed. Save again.
        // 4 : Void order succesful .
        // 5 : Void order failed. Try again.
        // 6 : Discontinue order succesful .
        // 7 : Discontinue order failed. Try again.
        // 8 : Undiscontinue order succesful .
        // 9 : Undiscontinue order failed. Try again.
        // 10 : Unvoid order successfull
        // 11 : Unvoid order failed. Try again
        public void sendModalityWorklist(Study s, OrderRequest orderRequest)
        {            
            Order order=s.order();
            Integer mwlStatus=s.getMwlStatus();
            String hl7blob=DicomUtils.createHL7Message(s,order,orderRequest);
            int status=DicomUtils.sendHL7Worklist(hl7blob);
            
            if (status==1){
                switch (orderRequest)
                {
                case Save_Order : if (mwlStatus.intValue()==0 || mwlStatus.intValue()==2)
                                        mwlStatus=1;
                                    else
                                        mwlStatus=3;
                                  break;
                case Void_Order : mwlStatus=5;
                                  break;
                case Unvoid_Order : mwlStatus=11;
                                  break;
                case Discontinue_Order : mwlStatus=7;
                                  break;
                case Undiscontinue_Order : mwlStatus=9;
                                  break;
                case Default : mwlStatus=0;
                                  break;    
                default : 
                                  break;    
                    
                }
                
            }else if (status==0){
                switch (orderRequest)
                {
                case Save_Order : if (mwlStatus.intValue()==0 || mwlStatus.intValue()==2)
                                            mwlStatus=2;
                                        else
                                            mwlStatus=4;
                                      break;
                case Void_Order : mwlStatus=6;
                                  break;
                case Unvoid_Order : mwlStatus=12;
                                  break;
                case Discontinue_Order : mwlStatus=8;
                                  break;
                case Undiscontinue_Order : mwlStatus=10;
                                  break;
                case Default : mwlStatus=0;
                                  break;    
                default : 
                                  break;                        
                }
            }
            s.setMwlStatus(mwlStatus);
            saveStudy(s);
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
