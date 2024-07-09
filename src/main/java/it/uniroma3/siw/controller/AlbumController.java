package it.uniroma3.siw.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

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

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Song;
import it.uniroma3.siw.service.AlbumService;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.SongService;
import it.uniroma3.siw.validation.AlbumValidator;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AlbumController {
	
	@Autowired AlbumService albumService;
	@Autowired ArtistService artistService;
	@Autowired SongService songService;
	@Autowired ImageService imageService;
	@Autowired CredentialsService credentialsService;
	
	@Autowired AlbumValidator albumValidator;
	
	@GetMapping("/album")
	public String albums(Model model) {
		model.addAttribute("albums", this.albumService.findAll());
		return "albums.html";
	}
	
	@GetMapping("/album/{id}")
	public String album(@PathVariable("id") Long id, @ModelAttribute("userDetails") UserDetails userDetails, Model model) {
		model.addAttribute("album", this.albumService.findById(id));
		if(userDetails!=null) {
			Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
			if(credentials.isAdmin())
				return "albumAdmin.html";
		}
		return "album.html";
	}
	
	@GetMapping("/addAlbum/{artistId}")
	public String addAlbum(@PathVariable("artistId") Long artistId, Model model) {
		Artist artist = this.artistService.findById(artistId);
		model.addAttribute("artist", artist);
		model.addAttribute("album", new Album());
		return "addAlbumForm.html";
	}
	
	@PostMapping("/addAlbum/{artistId}")
	public String addAlbumForm(@ModelAttribute("album")Album album, BindingResult bindingResult, @PathVariable("artistId") Long artistId, Model model) {
		Artist artist = this.artistService.findById(artistId);
		album.setArtist(artist);
		this.albumValidator.validate(album, bindingResult);
		if(bindingResult.hasErrors()) {
			model.addAttribute("artist", artist);
			return "addAlbumForm.html";
		}
		this.albumService.save(album);
		artist.getAlbums().add(album);
		this.artistService.save(artist);
		return "redirect:/addAlbum/song/" + album.getId();
	}
	
	@GetMapping("/removeSongAlbum/{albumId}/{songId}")
	public String removeSongAlbum(@PathVariable("albumId")Long albumId, @PathVariable("songId")Long songId) {
		Song song = this.songService.findById(songId);
		Album album = this.albumService.findById(albumId);
		song.setAlbum(null);
		album.getSongs().remove(song);
		this.songService.save(song);
		this.albumService.save(album);
		return "redirect:/addAlbum/song/" + albumId;
	}
	
	@GetMapping("/addSongAlbum/{albumId}/{songId}")
	public String addSongAlbum(@PathVariable("albumId")Long albumId, @PathVariable("songId")Long songId) {
		Song song = this.songService.findById(songId);
		Album oldAlbum = song.getAlbum();
		if(oldAlbum != null) {
			oldAlbum.getSongs().remove(song);
			this.albumService.save(oldAlbum);
		}
		Album album = this.albumService.findById(albumId);
		song.setAlbum(album);
		album.getSongs().add(song);
		this.songService.save(song);
		this.albumService.save(album);
		return "redirect:/addAlbum/song/" + albumId;
	}
	
	@GetMapping("/addAlbum/song/{id}")
	public String addAlbumSongs(@PathVariable("id")Long id, Model model) {
		Album album = this.albumService.findById(id);
		model.addAttribute("album", album);
		model.addAttribute("songs", this.songService.getSongsNotInAlbum(album));
		return "addAlbumSongs.html";
	}
	
	@GetMapping("/addAlbum/image/{id}")
	public String addArtistImage(@PathVariable("id") Long id, Model model) {
		Album album = this.albumService.findById(id);
		model.addAttribute("album", album);
		return "addAlbumImageForm.html";
	}
	
	@PostMapping("/addAlbum/image/{id}")
	public String addArtistImageForm(HttpServletRequest request, @RequestParam("image") MultipartFile file, 
			 @PathVariable("id") Long id, Model model)throws IOException, SerialException, SQLException {
		
		Album album = this.albumService.findById(id);
		Image oldImage = album.getImage();
		Image image = this.imageService.save(file);
		album.setImage(image);
		this.albumService.save(album);
		
		if(oldImage!=null)
			this.imageService.delete(oldImage);
		
		return "redirect:/album/" + id;
	}
	
	@GetMapping("/editAlbum/{albumId}")
	public String editAlbum(@PathVariable("albumId") Long albumId, Model model) {
		Album album = this.albumService.findById(albumId);
		model.addAttribute("album", album);
		return "editAlbumForm.html";
	}
	
	@PostMapping("/editAlbum/{albumId}")
	public String editAlbumForm(@ModelAttribute("album")Album tempAlbum, BindingResult bindingResult, @PathVariable("albumId") Long albumId, Model model) {
		Album album = this.albumService.findById(albumId);
		tempAlbum.setArtist(album.getArtist());
		this.albumValidator.validate(tempAlbum, bindingResult);
		if(bindingResult.hasErrors()) {
			return "addAlbumForm.html";
		}
		
		if(!(album.getName().equals(tempAlbum.getName())))
			album.setName(tempAlbum.getName());
		if(!(album.getDescription().equals(tempAlbum.getDescription())))
			album.setDescription(tempAlbum.getDescription());
		
		this.albumService.save(album);
		
		return "redirect:/addAlbum/song/" + album.getId();
	}
	
	@GetMapping("/admin/deleteAlbum/{albumId}")
	public String deleteAlbum(@PathVariable("albumId") Long albumId){
		Album album = this.albumService.findById(albumId);
		Iterable<Song> songs = album.getSongs();
		Iterator<Song> it = songs.iterator();
		while(it.hasNext()) {
			Song s = it.next();
			s.setAlbum(null);
			this.songService.save(s);
		}
		Long artistId = album.getArtist().getId();
		this.albumService.delete(album);
		return "redirect:/artist/" + artistId;
	}
	
}
