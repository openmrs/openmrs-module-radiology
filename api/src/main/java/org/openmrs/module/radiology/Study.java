package org.openmrs.module.radiology;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;

/**
 * A class that supports on openmrs's orders to make the module DICOM
 * compatible, corresponds to the table order_dicom_complment
 * 
 * @author Cortex
 */
public class Study {
	        
        public enum Modality {
           
		CR("Computed Radiography"),
                MR("Magnetic Resonance"),
                CT("Computed Tomography"),
                NM("Nuclear Medicine"),
                US("Ultrasound"),
                XA("X Ray");
                
                final private String fullName;
                Modality (String fullname){
                    this.fullName=fullname;
                }
                public String getFullName(){
                    return this.fullName;
                }
                public static List<String> getAllFullNames()
                {
                    List<String> fullNameList=new ArrayList<String>();
                    for (Modality s:Modality.values())
                        fullNameList.add(s.getFullName());
                    return fullNameList;
                }
	}

	// Performed Procedure Steps Statuses - Part 3 Annex C.4.14
	public static class PerformedStatuses {
		public static final int IN_PROGRESS = 0;
		public static final int DISCONTINUED = 1;
		public static final int COMPLETED = 2;

		public static boolean has(int x) {
			return !(string(x, false).compareTo("UNKNOWN") == 0);
		}

		// TODO localized
		public static String string(Integer x, Boolean localized) {
			switch (x) {
			case IN_PROGRESS:
				return localized ? localized("radiology.IN_PROGRESS")
						: "IN PROGRESS";
			case DISCONTINUED:
				return localized ? localized("radiology.DISCONTINUED")
						: "DISCONTINUED";
			case COMPLETED:
				return localized ? localized("radiology.COMPLETED")
						: "COMPLETED";
			default:
				return localized ? localized("general.unknown") : "UNKNOWN";
			}
		}

		public static int value(String s) {
			if (s.toLowerCase().contains("progress"))
				return IN_PROGRESS;
			if (s.compareToIgnoreCase("discontinued") == 0)
				return DISCONTINUED;
			if (s.compareToIgnoreCase("completed") == 0)
				return COMPLETED;
			return -1;
		}
	}

	// Priorities - Part 3 Annex C.4.11
	public static class Priorities {
		public static final int STAT = 0;
		public static final int HIGH = 1;
		public static final int ROUTINE = 2;
		public static final int MEDIUM = 3;
		public static final int LOW = 4;

		public static boolean has(int x) {
			return !(string(x, false).compareTo("UNKNOWN") == 0);
		}

		public static String string(Integer x, Boolean localized) {
			switch (x) {
			case STAT:
				return localized ? localized("radiology.STAT") : "STAT";
			case HIGH:
				return localized ? localized("radiology.HIGH") : "HIGH";
			case ROUTINE:
				return localized ? localized("radiology.ROUTINE") : "ROUTINE";
			case MEDIUM:
				return localized ? localized("radiology.MEDIUM") : "MEDIUM";
			case LOW:
				return localized ? localized("radiology.LOW") : "LOW";
			default:
				return localized ? localized("general.unknown") : "UNKNOWN";
			}
		}
	}

	// Scheduled Procedure Steps Statuses - Part 3 Annex C.4.10
	public static class ScheduledStatuses {
		public static final int SCHEDULED = 0;
		public static final int ARRIVED = 1;
		public static final int READY = 2;
		public static final int STARTED = 3;
		public static final int DEPARTED = 4;

		public static boolean has(int x) {
			return !(string(x, false).compareTo(localized("UNKNOWN")) == 0);
		}

		public static String string(Integer x, Boolean localized) {
			switch (x) {
			case SCHEDULED:
				return localized ? localized("radiology.SCHEDULED")
						: "SCHEDULED";
			case ARRIVED:
				return localized ? localized("radiology.ARRIVED") : "ARRIVED";
			case READY:
				return localized ? localized("radiology.READY") : "READY";
			case STARTED:
				return localized ? localized("radiology.STARTED") : "STARTED";
			case DEPARTED:
				return localized ? localized("radiology.DEPARTED") : "DEPARTED";
			default:
				return localized ? localized("general.unknown") : "UNKNOWN";
			}
		}
	}

	public static Study byUID(String uid) {
		String query = "from Study as s where s.uid = '" + uid + "'";
		return (Study) Context.getService(Main.class).get(query, true);
	}

	public static List<Study> get(List<Order> o) {
		ArrayList<Study> s = new ArrayList<Study>();
		for (Order o1 : o) {
			s.add(get(o1));
		}
		return s;
	}

	public static Study get(Order o) {
		return Context.getService(Main.class).getStudyByOrderId(o.getOrderId());
	}

	private static String localized(String code) {
		return Context.getMessageSourceService().getMessage(code);
	}

	static Main service() {
		return Context.getService(Main.class);
	}

	@SuppressWarnings("unchecked")
	public static List<Study> unassigned() {
		String query = "from Study as s where s.orderID = 0";
		return (List<Study>) Context.getService(Main.class).get(query, false);
	}

	private int id;
	private String uid;
	private int orderID;
	private int scheduledStatus = -1;
	private int performedStatus = -1;
	private int priority = -1;
	private int modality;
        
        private int mwlStatus;

	private User scheduler;

	private User performingPhysician;

	private User readingPhysician;

	public Study() {
		super();
	}

	public Study(int id, String uid, int orderID, int scheduledStatus,
			int performedStatus, int priority, int modality,
			User schedulerUserId, User performingPhysicianUserId,
			User readingPhysicianUserId) {
		super();
		this.id = id;
		this.uid = uid;
		this.orderID = orderID;
		this.scheduledStatus = scheduledStatus;
		this.performedStatus = performedStatus;
		this.priority = priority;
		this.modality = modality;
		this.scheduler = schedulerUserId;
		this.performingPhysician = performingPhysicianUserId;
		this.readingPhysician = readingPhysicianUserId;                
	}

	private PatientIdentifier doIdentifier(Integer patientId) {
		PatientIdentifier pi = new PatientIdentifier();
		PatientService ps = Context.getPatientService();
		pi.setIdentifier(patientId.toString());
		pi.setIdentifierType(ps.getPatientIdentifierType(2));
		pi.setPreferred(false);
		LocationService ls = Context.getLocationService();
		pi.setLocation(ls.getAllLocations().get(0));
		pi.setCreator(Context.getUserContext().getAuthenticatedUser());
		pi.setVoided(false);
		return pi;
	}

	private PersonName doPersonName(String name) {
		PersonName n = new PersonName();
		n.setGivenName(name.replace("^", " "));
		n.setPreferred(false);
		n.setCreator(Context.getUserContext().getAuthenticatedUser());
		n.setVoided(false);
		return n;
	}

	public int getId() {
		return id;
	}

	public int getModality() {
		return modality;
	}

	public int getOrderID() {
		return orderID;
	}

	public int getPerformedStatus() {
		return performedStatus;
	}

	public User getPerformingPhysician() {
		return performingPhysician;
	}

	public int getPriority() {
		return priority;
	}

	public User getReadingPhysician() {
		return readingPhysician;
	}

	public int getScheduledStatus() {
		return scheduledStatus;
	}

	public User getScheduler() {
		return scheduler;
	}

	public String getStatus(User u) {
		if (u.hasRole(Roles.ReferringPhysician, true))
			return statuses(true, true);
		if (u.hasRole(Roles.Scheduler, true))
			return statuses(true, false);
		if (u.hasRole(Roles.PerformingPhysician, true))
			return statuses(true, true);
		if (u.hasRole(Roles.ReadingPhysician, true))
			return statuses(false, true);
		return statuses(true, true);
	}

	public String getUid() {
		return uid;
	}
        
        public int getMwlStatus(){
            return mwlStatus;
        }

	public boolean isCompleted() {
		return performedStatus == PerformedStatuses.COMPLETED;
	}

	public boolean isScheduleable() {
		return !PerformedStatuses.has(performedStatus);
	}

	@SuppressWarnings("unchecked")
	public List<Obs> obs() {
                //String query = "from Obs as o where o.order.orderId = " + orderID;
                String innerQuery= "(Select oo.previousVersion from Obs as oo where oo.order.orderId="+orderID+" and oo.previousVersion IS NOT NULL)";
		String query = "from Obs as o where o.order.orderId = " + orderID+" and o.obsId NOT IN "+innerQuery;
		return (List<Obs>) Context.getService(Main.class).get(query, false);
	}

	public Order order() {
		String query = "from Order as o where o.orderId = " + orderID;
		return (Order) Context.getService(Main.class).get(query, true);
	}

	public String performing() {
		return getPerformingPhysician() == null ? "" : getPerformingPhysician()
				.getPersonName().getFullName();
	}

	private Order persistOrder(Patient p) {
		OrderService os = Context.getOrderService();
		Order o = new Order();
		o.setOrderType(Utils.getRadiologyOrderType().get(0));
		o.setConcept(Context.getConceptService().getAllConcepts().get(0));
		UserContext uc = Context.getUserContext();
		User user = uc.getAuthenticatedUser();
		o.setOrderer(user);
		o.setCreator(user);
		o.setDiscontinued(false);
		o.setVoided(false);
		o.setPatient(p);
		os.saveOrder(o);
		orderID = o.getOrderId();
		return o;
	}

	public Order persistOrderPatient(Integer patId, String patName) {
		Patient p = persistPatient(patName, patId);
		return persistOrder(p);
	}

	private Patient persistPatient(String patientName, Integer patientId) {
		PatientService ps = Context.getPatientService();
		Patient p = null;
		try {p = ps.getPatients(null, patientId.toString(), null, false).get(0);}
		catch (Throwable t) {}
		if (p == null) {
			p = new Patient();
			Person pe = persistPerson(doPersonName(patientName));
			p.setPersonId(pe.getPersonId());
			p.setCreator(Context.getUserContext().getAuthenticatedUser());
			p.setVoided(false);
			p.addIdentifier(doIdentifier(patientId));
			ps.savePatient(p);
		}
		return p;
	}

	private Person persistPerson(PersonName name) {
		PersonService ps = Context.getPersonService();
		Person p = new Person();
		p.setBirthdateEstimated(false);
		p.setDead(false);
		p.setCreator(Context.getUserContext().getAuthenticatedUser());
		p.setVoided(false);
		p.addName(name);
		ps.savePerson(p);
		clearSession();
		return p;
	}

	private void clearSession() {
		try{service().db().session().clear();}
		catch(Throwable t){t.printStackTrace();}
	}

	public String reading() {
		return getReadingPhysician() == null ? "" : getReadingPhysician()
				.getPersonName().getFullName();
	}

	public String scheduler() {
		return getScheduler() == null ? "" : getScheduler().getPersonName()
				.getFullName();
	}
        
        public String mwlStatus(){
            
            // -1 :Default
         String[] mwlMessages={    
        "Default ",     
        "In Sync : Save order successful.",
        "Out of Sync : Save order failed. Try Again!",         
        "In Sync : Update order successful.",
        "Out of Sync : Update order failed. Try again!",
        "In Sync : Void order successful.",             
        "Out of Sync : Void order failed. Try again!",
        "In Sync : Discontinue order successful.",
        "Out of Sync : Discontinue order failed. Try again!",
        "In Sync : Undiscontinue order successful.",
        "Out of Sync : Undiscontinue order failed. Try again!",
        "In Sync :  Unvoid order successfull",
        "Out of Sync :  Unvoid order failed. Try again"
         };
         if (mwlStatus==-1)
             return "Default";
         else
            return mwlMessages[mwlStatus];
        }
        
         public void setMwlStatus(int status){
             this.mwlStatus=status;
        }

	public void setId(int id) {
		this.id = id;
	}

	public void setModality(int modality) {
		this.modality = modality;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public void setPerformedStatus(int performedStatus) {
		this.performedStatus = performedStatus;
	}

	public void setPerformingPhysician(User performingPhysician) {
		this.performingPhysician = performingPhysician;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setReadingPhysician(User readingPhysician) {
		this.readingPhysician = readingPhysician;
	}

	public void setScheduledStatus(int scheduledStatus) {
		this.scheduledStatus = scheduledStatus;
	}

	public void setScheduler(User scheduler) {
		this.scheduler = scheduler;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * Fills null required values, to the moment orderer set to currentUser.<br/>
	 * Fills radiology order type. Fills concept if null.<br/>
	 * <br/>
	 * In this function goes all validation pre post request: <li>Scheduler is
	 * not allowed to schedule a completed procedure</li>
	 * 
	 * @param o
	 *            Order to be filled
	 * @param studyId
	 *            TODO
	 * @return Order modified
	 */
	public boolean setup(Order o, Integer studyId) {
		setId(studyId);

		User u = Context.getAuthenticatedUser();
		if (u.hasRole(Roles.ReferringPhysician, true) && o.getOrderer() == null)
			o.setOrderer(u);
		if (u.hasRole(Roles.Scheduler, true) && getScheduler() == null) {
			if (!isScheduleable()) {
				return false;
			} else {
				setScheduler(u);
			}

		}
		if (u.hasRole(Roles.PerformingPhysician, true)
				&& getPerformingPhysician() == null)
			setPerformingPhysician(u);
		if (u.hasRole(Roles.ReadingPhysician, true)
				&& getReadingPhysician() == null)
			setReadingPhysician(u);

		if (o.getStartDate() != null)
			setScheduledStatus(ScheduledStatuses.SCHEDULED);

		if (o.getOrderer() == null)
			o.setOrderer(u);
		o.setOrderType(Utils.getRadiologyOrderType().get(0));
		if (o.getConcept() == null)
			o.setConcept(Context.getConceptService().getConcept(1));
		return true;
	}

	private String statuses(boolean sched, boolean perf) {
		String ret = "";
		String scheduled = "";
		scheduled += ScheduledStatuses.string(scheduledStatus, true);
		ret += sched ? scheduled : "";
		String performed = "";
		performed += PerformedStatuses.string(performedStatus, true);
		ret += perf ? (sched ? " " : "") + performed : "";
		return ret;
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();

		Field[] fields = this.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				buff.append(fields[i].getName()).append(": ")
						.append(fields[i].get(this)).append(" ");
			} catch (IllegalAccessException ex) {
			} catch (IllegalArgumentException ex) {
			}

		}
		return buff.toString();
	}
}
