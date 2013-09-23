package org.openmrs.module.radiology.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.ObsService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.radiology.Main;
import org.openmrs.module.radiology.Study;
import org.openmrs.module.radiology.Utils;
import org.openmrs.obs.ComplexData;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.DrugEditor;
import org.openmrs.propertyeditor.EncounterEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.propertyeditor.OrderEditor;
import org.openmrs.propertyeditor.PersonEditor;
import org.openmrs.validator.ObsValidator;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ObsFormController {

	
	static Main service() {
		return Context.getService(Main.class);
	}

	@InitBinder
	void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, true));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true));
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor(true)); //allow for an empty boolean value
		binder.registerCustomEditor(Person.class, new PersonEditor());
		binder.registerCustomEditor(Order.class, new OrderEditor());
		binder.registerCustomEditor(Concept.class, new ConceptEditor());
		binder.registerCustomEditor(Drug.class, new DrugEditor());
		binder.registerCustomEditor(Location.class, new LocationEditor());
		binder.registerCustomEditor(Encounter.class, new EncounterEditor());
	}
	
	@RequestMapping(value = "/module/radiology/radiologyObs.form", method = RequestMethod.GET)
	protected ModelAndView getObs(
			@RequestParam(value = "orderId", required = false) Integer orderId,
			@RequestParam(value = "obsId", required = false) Integer obsId) {
		ModelAndView mav = new ModelAndView("module/radiology/obsForm");
		populate(mav, orderId,obsId);
		return mav;
	}

	private void populate(ModelAndView mav,
			Integer orderId, Integer obsId) {
		Obs obs = null;
		// Get previous obs
		List<Obs> prevs=null;
		ObsService os = Context.getObsService();
		OrderService or = Context.getOrderService();
		Study study=service().getStudyByOrderId(orderId);
		if (obsId != null) {
			obs = os.getObs(obsId);
			prevs=service().getStudyByOrderId(obs.getOrder().getOrderId()).obs();
		} else{
			obs = newObs(or.getOrder(orderId));
			prevs=study.obs();
		}
		
		mav.addObject("obs", obs);
		mav.addObject("studyUID", study.isCompleted() ? study.getUid() : null);
                if (study.isCompleted())
                {
                    Integer patID=or.getOrder(orderId).getPatient().getId();
                    String link=Utils.serversAddress()+":"+Utils.serversPort()+"/Oviyam2/viewer.html?serverName="+Utils.oviyamLocalServerName()+"&studyUID="+study.getUid()+"&patientID="+patID.toString();
                    mav.addObject("oviyamLink",link);                    
                }
                else
                    mav.addObject("oviyamLink",null);
		mav.addObject("prevs", prevs);
		mav.addObject("prevsSize", prevs.size());
	}

	private Obs newObs(Order order) {
		Obs obs;
		obs = new Obs();
		if (order != null) {
			obs.setOrder(order);
			obs.setPerson(order.getPatient());
			obs.setEncounter(order.getEncounter());
		}
		return obs;
	}

	@RequestMapping(value = "/module/radiology/radiologyObs.form", method = RequestMethod.POST)
	ModelAndView postObs(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "order", required = false) Integer orderId,
			@RequestParam(value = "obsId", required = false) Integer obsId,
			@ModelAttribute("obs") Obs obs, BindingResult errors) {
			HttpSession httpSession = request.getSession();
			new ObsValidator().validate(obs, errors);
			if (errors.hasErrors()) {
				ModelAndView mav = new ModelAndView(
						"module/radiology/obsForm");
				populate(mav, orderId, obsId);
				return mav;
			}
			if (Context.isAuthenticated() && !errors.hasErrors()) {
				ObsService os = Context.getObsService();
				try {
					// if the user is just editing the observation
					if (request.getParameter("saveObs") != null) {
						// TODO get reason from form when it is being saved along with the observation
						String reason=""; 
						
						if (obs.getConcept().isComplex()) {
							if (request instanceof MultipartHttpServletRequest) {
								MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
								MultipartFile complexDataFile = multipartRequest
										.getFile("complexDataFile");
								if (complexDataFile != null
										&& !complexDataFile.isEmpty()) {
									InputStream complexDataInputStream = complexDataFile
											.getInputStream();

									ComplexData complexData = new ComplexData(
											complexDataFile
													.getOriginalFilename(),
											complexDataInputStream);

									obs.setComplexData(complexData);

									// the handler on the obs.concept is called
									// with
									// the given complex data
									os.saveObs(obs, reason);

									complexDataInputStream.close();
								}
							}
						} else {
							os.saveObs(obs, reason);
						}

						httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
								"Obs.saved");
					}

					// if the user is voiding out the observation
					else if (request.getParameter("voidObs") != null) {
						String voidReason = request.getParameter("voidReason");
						if (obs.getObsId() != null
								&& (voidReason == null || voidReason.length() == 0)) {
							errors.reject("voidReason", "Obs.void.reason.empty");
							ModelAndView mav = new ModelAndView(
									"module/radiology/obsForm");
							populate(mav, orderId, obsId);
							return mav;
						}

						os.voidObs(obs, voidReason);
						httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
								"Obs.voidedSuccessfully");
					}

					// if this obs is already voided and needs to be unvoided
					else if (request.getParameter("unvoidObs") != null) {
						os.unvoidObs(obs);
						httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
								"Obs.unvoidedSuccessfully");
					}

				} catch (APIException e) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
							e.getMessage());
					ModelAndView mav = new ModelAndView(
							"module/radiology/obsForm");
					populate(mav, orderId, obsId);
					return mav;
				} catch (IOException e) {
					ModelAndView mav = new ModelAndView(
							"module/radiology/obsForm");
					populate(mav, orderId, obsId);
					return mav;
				}

			}
		return new ModelAndView(
				"redirect:/module/radiology/radiologyOrder.list");
	}
}
