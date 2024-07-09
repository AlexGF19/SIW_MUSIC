package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Song;
import it.uniroma3.siw.repository.SongRepository;

@Service
public class SongService {

	@Autowired SongRepository songRepository;
	
	public Iterable<Song> getSongsByGenre(Long genreId) {
		return this.songRepository.getSongsByGenre(genreId);
	}
	
	public Iterable<Song> findAll(){
		return this.songRepository.findAll();
	}
	
	public Song findById(Long id) {
		return this.songRepository.findById(id).get();
	}
	
	
	public boolean existsByNameAndArtist(String name, Artist artist) {
		return this.songRepository.existsByNameAndArtist(name, artist);
	}
	
	public void save(Song song) {
		this.songRepository.save(song);
	}
	
	public void delete(Song song) {
		this.songRepository.delete(song);
	}
	
	public Iterable<Song> getSongsNotInAlbum(Album album){
		return this.songRepository.getSongsNotInAlbum(album.getArtist().getId(), album.getId());
	}
	
	public Iterable<Song> searchSongQuery(String param){
		return this.songRepository.searchSongQuery("%" + param + "%");
	}
	
	public Song getLatestSong() {
		return this.songRepository.getLatestSong();
	}
}
