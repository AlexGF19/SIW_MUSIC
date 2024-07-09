package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Service
public class ArtistService {
	
	@Autowired ArtistRepository artistRepository;
	
	public void save(Artist artist) {
		this.artistRepository.save(artist);
	}
	
	public void delete(Artist artist) {
		this.artistRepository.delete(artist);
	}

	public boolean existsByName(Artist artist) {
		return this.artistRepository.existsByName(artist.getName());
	}
	
	public Artist findById(Long id) {
		return this.artistRepository.findById(id).get();
	}
	
	public Iterable<Artist> findAll(){
		return this.artistRepository.findAll();
	}
	
	public Iterable<Artist> searchArtistQuery(String param){
		return this.artistRepository.searchArtistQuery("%" + param + "%");
	}
	
	public Artist getLatestArtist() {
		return this.artistRepository.getLatestArtist();
	}
}
