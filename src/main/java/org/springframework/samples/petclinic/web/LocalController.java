
package org.springframework.samples.petclinic.web;

import java.awt.Window;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Fiesta;
import org.springframework.samples.petclinic.model.Local;
import org.springframework.samples.petclinic.model.Propietario;
import org.springframework.samples.petclinic.service.FiestaService;
import org.springframework.samples.petclinic.service.LocalService;
import org.springframework.samples.petclinic.service.PropietarioService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LocalController {

	private final LocalService			localService;
	private final PropietarioService	propietarioService;
	private final FiestaService fiestaService;

	@Autowired
	public LocalController(final LocalService localService, final PropietarioService propietarioService, final FiestaService fiestaService) {
		this.localService = localService;
		this.propietarioService = propietarioService;
		this.fiestaService=fiestaService;
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
			"/local/{localId}/fiestas"
		})
		public String verSolicitudes(@PathVariable("localId") final int localId,final Map<String, Object> model) {
			
			Collection<Fiesta> fiestas = this.fiestaService.findFiestasByLocalId(localId);
			
			model.put("fiestas", fiestas);
			return "fiestas/listaFiestas";
		}
	
	@GetMapping(value= {"/local/fiesta/{fiestaId}/aceptar"})
	public String aceptarSolicitud(@PathVariable("fiestaId") int fiestaId,final Map<String, Object> model) {
		Fiesta fiesta=fiestaService.aceptarSolicitud(fiestaId);
		Collection<Fiesta> fiestas = this.fiestaService.findFiestasByLocalId(fiesta.getLocal().getId());
		
		model.put("fiestas", fiestas);
		return "fiestas/listaFiestas";
	}
	
	@GetMapping(value= {"/local/fiesta/{fiestaId}/denegar"})
	public String denegarSolicitud(@PathVariable("fiestaId") int fiestaId,final Map<String, Object> model) {
		Fiesta fiesta=fiestaService.denegarSolicitud(fiestaId);
		Collection<Fiesta> fiestas = this.fiestaService.findFiestasByLocalId(fiesta.getLocal().getId());
		
		model.put("fiestas", fiestas);
		return "fiestas/listaFiestas";
	}

}
