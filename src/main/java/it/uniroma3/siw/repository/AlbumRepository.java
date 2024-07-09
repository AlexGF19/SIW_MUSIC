package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.model.Artist;

public interface AlbumRepository extends CrudRepository<Album, Long>{
	
	public boolean existsByNameAndArtist(String name, Artist artist);
	
	@Query("SELECT a FROM Album a WHERE LOWER(a.name) LIKE LOWER(:param)")
	public Iterable<Album> searchAlbumQuery(@Param("param") String param);

	@Query("SELECT a FROM Album a WHERE a.id = (SELECT MAX(al.id) FROM Album al)")
	public Album getLatestAlbum();
	
}
