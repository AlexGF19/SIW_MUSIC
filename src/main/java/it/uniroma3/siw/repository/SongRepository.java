package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Song;

public interface SongRepository extends CrudRepository<Song, Long>{

	@Query("SELECT s FROM Song s WHERE s.genre.id = :genreId")
	public Iterable<Song> getSongsByGenre(Long genreId);
	
	public boolean existsByNameAndArtist(String name, Artist artist);
	
	@Query("SELECT s FROM Song s WHERE s.artist.id=:artistId and s.id NOT IN (SELECT s.id FROM Song s WHERE s.album.id = :albumId)")
	public Iterable<Song> getSongsNotInAlbum(@Param("artistId") Long artistId, @Param("albumId") Long albumId);
	
	@Query("SELECT s FROM Song s WHERE LOWER(s.name) LIKE LOWER(:param)")
	public Iterable<Song> searchSongQuery(@Param("param") String param);
	
	@Query("SELECT s FROM Song s WHERE s.id = (SELECT MAX(so.id) FROM Song so)")
	public Song getLatestSong();

}
