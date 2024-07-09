package it.uniroma3.siw.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {
	
	@Autowired UserService userService;
	@Autowired CredentialsService credentialsService;
	@Autowired ImageService imageService;
	
	@GetMapping("/editProfile/{id}")
	public String editProfile(@PathVariable("id") Long id, @ModelAttribute("userDetails")UserDetails userDetails, Model model) {
		User user = this.userService.findById(id);
		if(user.getCredentials().getUsername().equals(userDetails.getUsername())
				|| this.credentialsService.getCredentials(userDetails.getUsername()).isAdmin()) {
			model.addAttribute("user", user);
			return "editProfileForm.html";
		}
		return "redirect:/";
	}
	
	@PostMapping("/editProfile/{id}")
	public String editProfileForm(@ModelAttribute("user") User tempUser, @PathVariable("id") Long id, @ModelAttribute("userDetails")UserDetails userDetails, Model model) {
		User user = this.userService.findById(id);
		if(!(user.getName().equals(tempUser.getName())))
			user.setName(tempUser.getName());
		if(!(user.getSurname().equals(tempUser.getSurname())))
			user.setSurname(tempUser.getSurname());
		if(!(user.getBirthDate().equals(tempUser.getBirthDate())))
			user.setBirthDate(tempUser.getBirthDate());
		this.userService.save(user);
		return "redirect:/profile/" + userDetails.getUsername();
	}
	
	@GetMapping("/addProfilePic/{id}")
	public String addProfilePic(@ModelAttribute("userDetails") UserDetails userDetails, @PathVariable("id") Long id, Model  model) {
		User user = this.userService.findById(id);
		if(user.getCredentials().getUsername().equals(userDetails.getUsername())
				|| this.credentialsService.getCredentials(userDetails.getUsername()).isAdmin()) {
			model.addAttribute("user", user);
			return "profilePicForm.html";
		}
		return "redirect:/";
	}
	
	@PostMapping("/addProfilePic/{id}")
	public String addProfilePicForm(HttpServletRequest request, @RequestParam("image") MultipartFile file, 
			@ModelAttribute("userDetails") UserDetails userDetails, @PathVariable("id") Long id, Model model)throws IOException, SerialException, SQLException{
		User user = this.userService.findById(id);
		Image oldImage = user.getImage();
	    Image image = this.imageService.save(file);
	     user.setImage(image);
	     this.userService.save(user);
	     
	     if(oldImage!=null)
				this.imageService.delete(oldImage);
	     
	     return "redirect:/profile/" + userDetails.getUsername();
	}
	
	@GetMapping("/deleteUser/{id}")
	public String deleteCook(@PathVariable("id") Long id , @ModelAttribute("userDetails") UserDetails userDetails) {
		User user = this.userService.findById(id);
		if(user.getCredentials().getUsername().equals(userDetails.getUsername())
				|| this.credentialsService.getCredentials(userDetails.getUsername()).isAdmin()) {
			this.userService.delete(user);
		}
		return "redirect:/logout";
	}
}
