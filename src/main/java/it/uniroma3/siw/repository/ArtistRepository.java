package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.model.Artist;

public interface ArtistRepository extends CrudRepository<Artist, Long>{

	public boolean existsByName(String name);
	
	@Query("SELECT a FROM Artist a WHERE LOWER(a.name) LIKE LOWER(:param)")
	public Iterable<Artist> searchArtistQuery(@Param("param") String param);

	@Query("SELECT a FROM Artist a WHERE a.id = (SELECT MAX(ar.id) FROM Artist ar)")
	public Artist getLatestArtist();
	
}
