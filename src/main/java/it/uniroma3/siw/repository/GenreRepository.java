package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Genre;

public interface GenreRepository extends CrudRepository<Genre, Long>{

	public boolean existsByName(String name);
		
	@Query("SELECT g FROM Genre g WHERE g.id NOT IN(SELECT s.genre.id FROM Song s WHERE s.id =:songId)")
	public Iterable<Genre> getGenreNotInSong(@Param("songId")Long songId);
	
}
