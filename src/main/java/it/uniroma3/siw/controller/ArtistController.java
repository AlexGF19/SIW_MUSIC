package it.uniroma3.siw.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.validation.ArtistValidator;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ArtistController {
	
	@Autowired ArtistService artistService;
	@Autowired ImageService imageService;
	@Autowired CredentialsService credentialsService;
	
	@Autowired ArtistValidator artistValidator;
	
	@GetMapping("/artist")
	public String artists(Model model) {
		model.addAttribute("artists", this.artistService.findAll());
		return "artists.html";
	}
	
	@GetMapping("/artist/{id}")
	public String artist(@PathVariable("id") Long id, @ModelAttribute("userDetails") UserDetails userDetails, Model model) {
		Artist artist = this.artistService.findById(id);
		model.addAttribute("artist", artist);
		if(userDetails!=null) {
			Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
			if(credentials.isAdmin())
				return "artistAdmin.html";
		}
		return "artist.html";
	}
	
	@GetMapping("/addArtist")
	public String addArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "addArtistForm.html";
	}
	
	@PostMapping("/addArtist")
	public String addArtist(@ModelAttribute("artist") Artist artist, BindingResult bindingResult) {
		this.artistValidator.validate(artist, bindingResult);
		if(bindingResult.hasErrors())
			return "addArtistForm.html";
		this.artistService.save(artist);
		return "redirect:/addArtist/image/" + artist.getId();
	}
	
	@GetMapping("/addArtist/image/{id}")
	public String addArtistImage(@PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.findById(id);
		model.addAttribute("artist", artist);
		return "addArtistImageForm.html";
	}
	
	@PostMapping("/addArtist/image/{id}")
	public String addArtistImageForm(HttpServletRequest request, @RequestParam("image") MultipartFile file, 
			 @PathVariable("id") Long id, Model model)throws IOException, SerialException, SQLException {
		Artist artist = this.artistService.findById(id);
		Image oldImage = artist.getImage();
		Image image = this.imageService.save(file);
		artist.setImage(image);
		this.artistService.save(artist);
		
		if(oldImage!=null)
			this.imageService.delete(oldImage);
		
		return "redirect:/artist/" + id;
	}
	
	@GetMapping("/editArtist/{artistId}")
	public String editArtist(@PathVariable("artistId") Long artistId, Model model) {
		Artist artist = this.artistService.findById(artistId);
		model.addAttribute("artist", artist);
		return "editArtistForm.html";
	}
	
	@PostMapping("/editArtist/{artistId}")
	public String editArtistForm(@ModelAttribute("artist") Artist tempArtist, @PathVariable("artistId") Long artistId, Model model) {
		Artist artist = this.artistService.findById(artistId);
		if(!(artist.getBio().equals(tempArtist.getBio())))
			artist.setBio(tempArtist.getBio());
		if(!(artist.getName().equals(tempArtist.getName())))
			artist.setName(tempArtist.getName());
		this.artistService.save(artist);
		return "redirect:/artist/" + artistId;
	}
	
	@GetMapping("/admin/deleteArtist/{artistId}")
	public String deleteArtist(@PathVariable("artistId") Long artistId) {
		Artist artist = this.artistService.findById(artistId);
		this.artistService.delete(artist);
		return "redirect:/";
	}
	

	
}
