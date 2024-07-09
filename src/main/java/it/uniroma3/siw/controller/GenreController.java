package it.uniroma3.siw.controller;

import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Genre;
import it.uniroma3.siw.model.Song;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.GenreService;
import it.uniroma3.siw.service.SongService;
import it.uniroma3.siw.validation.GenreValidator;
import jakarta.validation.Valid;

@Controller
public class GenreController {
	
	@Autowired GenreService genreService;
	@Autowired SongService songService;
	@Autowired CredentialsService credentialsService;
	
	@Autowired GenreValidator genreValidator;
	
	@GetMapping("/genre")
	public String genres(Model model) {
		model.addAttribute("genres", this.genreService.findAll());
		return "genres.html";
	}
	
	@GetMapping("/genre/{id}")
	public String genre(@PathVariable("id") Long id, @ModelAttribute("userDetails") UserDetails userDetails, Model model) {
		model.addAttribute("genre", this.genreService.findById(id));
		model.addAttribute("songs", this.songService.getSongsByGenre(id));
		if(userDetails!=null) {
			Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
			if(credentials.isAdmin())
				return "genreAdmin.html";
		}
		return "genre.html";
	}
	
	@GetMapping("/admin/addGenre")
	public String addGenre(Model model) {
		model.addAttribute("genre", new Genre());
		return "addGenreForm.html";
	}
	
	@PostMapping("/admin/addGenre")
	public String addGenreForm(@ModelAttribute("genre") Genre genre, BindingResult bindingResult) {
		this.genreValidator.validate(genre, bindingResult);
		if(bindingResult.hasErrors())
			return "addGenreForm.html";
		this.genreService.save(genre);
		return "redirect:/genre/" + genre.getId();
	}
	
	@GetMapping("/admin/editGenre/{genreId}")
	public String editGenre(@PathVariable("genreId") Long genreId, Model model) {
		model.addAttribute("genre", this.genreService.findById(genreId));
		return "editGenreForm.html";
	}
	
	@PostMapping("/admin/editGenre/{genreId}")
	public String editGenreForm(@Valid @ModelAttribute("genre") Genre tempGenre, BindingResult bindingResult, @PathVariable("genreId") Long genreId) {
		Genre genre = this.genreService.findById(genreId);
		this.genreValidator.validate(tempGenre, bindingResult);
		if(bindingResult.hasErrors())
			return "addGenreForm.html";
		if(!(genre.getName().equals(tempGenre.getName())))
			genre.setName(tempGenre.getName());
		this.genreService.save(genre);
		return "redirect:/genre/" + genre.getId();
	}
	
	@GetMapping("/admin/deleteGenre/{genreId}")
	public String deleteAlbum(@PathVariable("genreId") Long genreId){
		Genre genre = this.genreService.findById(genreId);
		ArrayList<Song> songs = (ArrayList<Song>) this.songService.getSongsByGenre(genreId);
		Iterator<Song> it = songs.iterator();
		while(it.hasNext()) {
			Song s = it.next();
			s.setGenre(null);
			this.songService.save(s);
		}
		this.genreService.delete(genre);
		return "redirect:/";
	}
}
