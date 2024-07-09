package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.AlbumRepository;

@Service
public class AlbumService {
	
	@Autowired AlbumRepository albumRepository;
	
	public Iterable<Album> findAll(){
		return this.albumRepository.findAll();
	}
	
	public Album findById(Long id) {
		return this.albumRepository.findById(id).get();
	}
	
	public boolean existsByNameAndArtist(String name, Artist artist) {
		return this.albumRepository.existsByNameAndArtist(name, artist);
	}
	
	public void save(Album album) {
		this.albumRepository.save(album);
	}
	
	public void delete(Album album) {
		this.albumRepository.delete(album);
	}
	
	public Iterable<Album> searchAlbumQuery(String param){
		return this.albumRepository.searchAlbumQuery("%" + param + "%");
	}
	
	public Album getLatestAlbum() {
		return this.albumRepository.getLatestAlbum();
	}
}
