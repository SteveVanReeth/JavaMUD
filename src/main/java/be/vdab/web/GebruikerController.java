package be.vdab.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import be.vdab.entities.Gebruiker;
import be.vdab.exceptions.EmailAdresAlInGebruikException;
import be.vdab.exceptions.GebruikerHeeftNogKaraktersException;
import be.vdab.exceptions.VerkeerdeEmailAdresException;
import be.vdab.services.GebruikerService;
import be.vdab.valueobjects.EmailAdres;
import javax.servlet.http.HttpSession;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.InitBinder;

@Controller
@RequestMapping("/gebruiker")
public class GebruikerController {

	private final GebruikerService gebruikerService;

	@Autowired
	public GebruikerController(GebruikerService gebruikerService) {
		this.gebruikerService = gebruikerService;
	}

	@RequestMapping(value = "toevoegen", method = RequestMethod.GET)
	public ModelAndView createForm() {
		return new ModelAndView("gebruikers/toevoegen", "gebruiker",
				new Gebruiker());
	}

	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid Gebruiker gebruiker, BindingResult bindingResult, 
                HttpSession session) {
		if (!bindingResult.hasErrors()) {
			try {
				gebruikerService.create(gebruiker);
                                //session.setAttribute("gebruiker", gebruiker);
				return "redirect:/aanmelden";
			} catch (EmailAdresAlInGebruikException e) {
				bindingResult.rejectValue("emailAdres",
						"EmailAdresAlInGebruikException");
			} catch (VerkeerdeEmailAdresException ex) {
				bindingResult.rejectValue("emailAdres",
						"VerkeerdeEmailAdresException");
			}
		}
		return "gebruikers/toevoegen";
	}

	@RequestMapping
	public ModelAndView showGebruiker() {
		Gebruiker gebruiker = this.getCurrentGebruiker();
		ModelAndView modelAndView = new ModelAndView("gebruikers/gebruiker",
				"gebruiker", gebruiker);
		return modelAndView;
	}

	public Gebruiker getCurrentGebruiker() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		EmailAdres emailAdres = new EmailAdres(authentication.getName());
		return gebruikerService.findByEmail(emailAdres);
	}

	@RequestMapping(value = "{id}/wijzigen", method = RequestMethod.GET)
	public ModelAndView updateForm(@PathVariable long id) {
		Gebruiker gebruiker = this.getCurrentGebruiker();
		if (gebruiker == null) {
			return new ModelAndView("redirect:/gebruikers");
		}
		return new ModelAndView("gebruikers/wijzigen", "gebruiker", gebruiker);
	}

	@RequestMapping(value = "{id}/wijzigen", method = RequestMethod.PUT)
	public String update(@PathVariable long id, @Valid Gebruiker gebruiker,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "gebruikers/wijzigen";
		}
		try {
			gebruiker.setId(id);
                        gebruikerService.update(gebruiker);
                        return "redirect:/gebruiker";
		} catch (EmailAdresAlInGebruikException e) {
			bindingResult.rejectValue("emailAdres",
					"EmailAdresAlInGebruikException");
			return "gebruikers/wijzigen";
		}
	}

	@RequestMapping(value = "verwijderd", method = RequestMethod.GET, params = {
			"id", "emailAdres" })
	public String delete() {
		return "gebruikers/verwijderd";
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "{id}/verwijderen")
	public ModelAndView delete(@PathVariable long id, HttpSession session) {
		Gebruiker gebruiker = gebruikerService.read(id);
                if (gebruiker == null) {
			return new ModelAndView("redirect:/");
		}
		ModelAndView modelAndView = new ModelAndView();
		try {	
			modelAndView.setViewName("gebruikers/verwijderd");
			modelAndView.addObject("id", id);
			modelAndView.addObject("emailAdres", gebruiker.getEmailAdres());
                        gebruikerService.delete(id);
                        //gebruiker uit sessie verwijderen
                        session.invalidate();
		} catch (GebruikerHeeftNogKaraktersException e) {
			modelAndView.setViewName("gebruikers/foutVerwijderd");
			modelAndView.addObject("id", id);
			modelAndView.addObject("emailAdres", gebruiker.getEmailAdres());
                        /*modelAndView.addObject("fout",
					"Gebruiker is niet verwijderd, het bevat nog karakters");
                        */
                }
		return modelAndView;
	}
        
        @InitBinder("gebruiker")
        public void initBinderGebruiker(DataBinder dataBinder){
            Gebruiker gebruiker = (Gebruiker) dataBinder.getTarget();
            if(gebruiker.getEmailAdres() == null){
                gebruiker.setEmailAdres(new EmailAdresForm());
            }
            else {
                gebruiker.setEmailAdres(new EmailAdresForm(gebruiker.getEmailAdres()));
            }
        }

}
