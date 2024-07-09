package it.uniroma3.siw.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

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
import it.uniroma3.siw.model.Genre;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Song;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.GenreService;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.SongService;
import it.uniroma3.siw.validation.SongValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class SongController {

	@Autowired SongService songService;
	@Autowired ArtistService artistService;
	@Autowired ImageService imageService;
	@Autowired GenreService genreService;
	@Autowired CredentialsService credentialsService;
	
	@Autowired SongValidator songValidator;
	
	@GetMapping("/song")
	public String songs(Model model) {
		model.addAttribute("songs", this.songService.findAll());
		return "songs.html";
	}
	
	@GetMapping("/song/{id}")
	public String song(@PathVariable("id") Long id, @ModelAttribute("userDetails") UserDetails userDetails, Model model) {
		Song song = this.songService.findById(id);
		model.addAttribute("song", song);
		if(userDetails!=null) {
			Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
			if(credentials.isAdmin())
				return "songAdmin.html";
		}
		return "song.html";
	}
	
	@GetMapping("/addSong/{artistId}")
	public String addSong(@PathVariable("artistId") Long artistId, Model model) {
		model.addAttribute("artist", this.artistService.findById(artistId));
		model.addAttribute("song", new Song());
		return "addSongForm.html";
	}
	
	@PostMapping("/addSong/{artistId}")
	public String addSongForm(@Valid @ModelAttribute("song") Song song, BindingResult bindingResult, 
			@PathVariable("artistId") Long artistId, Model model)  {
		Artist artist = this.artistService.findById(artistId);
		song.setArtist(artist);
		
		this.songValidator.validate(song, bindingResult);
		if(bindingResult.hasErrors()) {
			model.addAttribute("artist", artist);
			return "addSongForm.html";
		}
		this.songService.save(song);
		artist.getSongs().add(song);
		this.artistService.save(artist);
		return "redirect:/addSong/genre/" + song.getId();
		
	}
	
	@GetMapping("/addSong/genre/{songId}")
	public String addSongGenres(@PathVariable("songId") Long songId, Model model) {
		Song song = this.songService.findById(songId);
		model.addAttribute("song", song);
		ArrayList<Genre> genres = (ArrayList<Genre>)this.genreService.getGenreNotInSong(song);
		if(genres.isEmpty())
			model.addAttribute("genres", this.genreService.findAll());
		else
			model.addAttribute("genres", this.genreService.getGenreNotInSong(song));

		return "addSongGenre.html";
	}
	
	@GetMapping("/addSong/genre/{songId}/{genreId}")
	public String addSongGenre(@PathVariable("songId") Long songId, @PathVariable("genreId") Long genreId) {
		Song song = this.songService.findById(songId);
		Genre genre = this.genreService.findById(genreId);
		song.setGenre(genre);
		this.songService.save(song);
		return "redirect:/addSong/image/" + songId;
	}
	
	@GetMapping("/addSong/image/{songId}")
	public String addArtistImage(@PathVariable("songId") Long songId, Model model) {
		Song song = this.songService.findById(songId);
		model.addAttribute("song", song);
		return "addSongImageForm.html";
	}
	
	@PostMapping("/addSong/image/{songId}")
	public String addArtistImageForm(HttpServletRequest request, @RequestParam("image") MultipartFile file, 
			 @PathVariable("songId") Long songId, Model model)throws IOException, SerialException, SQLException {
		
		Song song = this.songService.findById(songId);
		
		Image oldImage = song.getImage();
		
		Image image = this.imageService.save(file);
		song.setImage(image);
		this.songService.save(song);
		
		if(oldImage!=null)
			this.imageService.delete(oldImage);
		
		return "redirect:/song/" + songId;
	}
	
	@GetMapping("/editSong/{songId}")
	public String editSong(@PathVariable("songId") Long songId, Model model) {
		model.addAttribute("song", this.songService.findById(songId));
		return "editSongForm.html";
	}
	
	@PostMapping("/editSong/{songId}")
	public String editSongForm(@Valid @ModelAttribute("song") Song tempSong, BindingResult bindingResult, @PathVariable("songId") Long songId, Model model) {
		Song song = this.songService.findById(songId);
		tempSong.setArtist(song.getArtist());
		this.songValidator.validate(tempSong, bindingResult);
		if(bindingResult.hasErrors()) 
			return "editSongForm.html";
		
		if(!(song.getName().equals(tempSong.getName())))
			song.setName(tempSong.getName());
		if(!(song.getInfo().equals(tempSong.getInfo())))
			song.setInfo(tempSong.getInfo());
		if(!(song.getLyrics().equals(tempSong.getLyrics())))
			song.setLyrics(tempSong.getLyrics());
		if(!(song.getMeaning().equals(tempSong.getMeaning())))
			song.setMeaning(tempSong.getMeaning());
		
		this.songService.save(song);
		
		return "redirect:/addSong/genre/" + song.getId();
		
	}
	
	@GetMapping("/admin/deleteSong/{songId}")
	public String deleteSong(@PathVariable("songId") Long songId) {
		Song song = this.songService.findById(songId);
		Long artistId = song.getArtist().getId();
		this.songService.delete(song);
		return "redirect:/artist/" + artistId;
	}
	
	
}
