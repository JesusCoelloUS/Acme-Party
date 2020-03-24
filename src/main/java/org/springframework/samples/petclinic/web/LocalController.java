
package org.springframework.samples.petclinic.web;

import java.util.Collection;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Local;
import org.springframework.samples.petclinic.model.Propietario;
import org.springframework.samples.petclinic.service.LocalService;
import org.springframework.samples.petclinic.service.PropietarioService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LocalController {

	private static final String			VIEWS_CAUSE_CREATE_FORM	= "locales/createLocalForm";
	private final LocalService			localService;
	private final PropietarioService	propietarioService;


	@Autowired
	public LocalController(final LocalService localService, final PropietarioService propietarioService) {
		this.localService = localService;
		this.propietarioService = propietarioService;
	}

	@GetMapping(value = {
		"/locales/{localId}"
	})
	public ModelAndView showLocal(@PathVariable("localId") final int localId) {
		ModelAndView mav = new ModelAndView("locales/localDetails");
		mav.addObject(this.localService.findLocalById(localId));
		return mav;
	}

	@GetMapping(value = {
		"/locales/buscar"
	})
	public String formularioBuscarLocales(final Map<String, Object> model) {

		Local local = new Local();
		model.put("local", local);

		return "locales/buscarLocales";
	}

	@GetMapping(value = "/locales")
	public String buscarLocales(final Local local, final BindingResult result, final Map<String, Object> model) {

		if (local.getDireccion() == null) {
			local.setDireccion("");
		}

		// find owners by last name
		Collection<Local> results = this.localService.findByDireccion(local.getDireccion());
		if (results.isEmpty()) {
			result.rejectValue("direccion", "notFound", "not found");
			return "locales/buscarLocales";
		}
		//		else if (results.size() == 1) {
		//			Local res = results.iterator().next();
		//			return "redirect:/locales/" + res.getId();}
		else {
			model.put("locales", results);
			return "locales/listaLocales";
		}
	}

	@GetMapping(value = {
		"/propietario/locales"
	})
	public String verMisLocales(final Map<String, Object> model) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Propietario p = this.propietarioService.findByUsername(username);
		Collection<Local> locales = this.localService.findByPropietarioId(p.getId());
		model.put("locales", locales);

		return "locales/listaLocales";
	}

	@GetMapping(value = {
		"/locales/new"
	})
	public String initCreationForm(final Map<String, Object> model) {
		Local local = new Local();
		model.put("local", local);
		return LocalController.VIEWS_CAUSE_CREATE_FORM;
	}

	@PostMapping(value = {
		"/locales/new"
	})
	public String processNewCauseForm(@Valid final Local local, final BindingResult result, final Map<String, Object> model) {

		if (result.hasErrors()) {
			model.put("local", local);
			return LocalController.VIEWS_CAUSE_CREATE_FORM;
		} else {
			local.setDecision("PENDIENTE");
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			Propietario p = this.propietarioService.findByUsername(username);
			local.setPropietario(p);
			this.localService.saveLocal(local);
			return "redirect:/propietario/locales";
		}
	}

}
