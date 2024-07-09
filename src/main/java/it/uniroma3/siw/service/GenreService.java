package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Genre;
import it.uniroma3.siw.model.Song;
import it.uniroma3.siw.repository.GenreRepository;

@Service
public class GenreService {
	
	@Autowired GenreRepository genreRepository;
	
	public Iterable<Genre> findAll(){
		return this.genreRepository.findAll();
	}
	
	public Genre findById(Long id) {
		return this.genreRepository.findById(id).get();
	}
	
	public boolean exsistsByName(Genre genre) {
		return this.genreRepository.existsByName(genre.getName());
	}
	
	public void save(Genre genre) {
		this.genreRepository.save(genre);
	}
	
	public void delete(Genre genre) {
		this.genreRepository.delete(genre);
	}
	
	public Iterable<Genre> getGenreNotInSong(Song song){
		return this.genreRepository.getGenreNotInSong(song.getId());
	}
}
