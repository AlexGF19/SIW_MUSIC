package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Song;
import it.uniroma3.siw.service.AlbumService;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.SongService;

@Controller
public class SearchController {
	
	@Autowired SongService songService;
	@Autowired ArtistService artistService;
	@Autowired AlbumService albumService;
	
	
	@GetMapping("/searchSong")
	public String searchSong(@RequestParam("prefix") String prefix, Model model) {
		Iterable<Song> songs = this.songService.searchSongQuery(prefix);
		model.addAttribute("songs", songs);
		model.addAttribute("prefix", prefix);
		return "searchResultsSong.html";
	}
	
	@GetMapping("/searchArtist")
	public String searchArtist(@RequestParam("prefix") String prefix, Model model) {
		Iterable<Artist> artists = this.artistService.searchArtistQuery(prefix);
		model.addAttribute("artists", artists);
		model.addAttribute("prefix", prefix);
		return "searchResultsArtist.html";
	}
	
	@GetMapping("/searchAlbum")
	public String searchAlbum(@RequestParam("prefix") String prefix, Model model) {
		Iterable<Album> albums = this.albumService.searchAlbumQuery(prefix);
		model.addAttribute("albums", albums);
		model.addAttribute("prefix", prefix);
		return "searchResultsAlbum.html";
	}
	
	@GetMapping("/search")
	public String search(@RequestParam("prefix") String prefix, Model model) {
		Iterable<Song> songs = this.songService.searchSongQuery(prefix);
		model.addAttribute("songs", songs);
		Iterable<Artist> artists = this.artistService.searchArtistQuery(prefix);
		model.addAttribute("artists", artists);
		Iterable<Album> albums = this.albumService.searchAlbumQuery(prefix);
		model.addAttribute("albums", albums);
		model.addAttribute("prefix", prefix);
		return "searchResults.html";
	}
}
