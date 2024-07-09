package it.uniroma3.siw.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Song;
import it.uniroma3.siw.service.SongService;

@Component
public class SongValidator implements Validator{
	
	@Autowired SongService songService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Song.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Song song = (Song) target;

		if(this.songService.existsByNameAndArtist(song.getName(), song.getArtist()))
			errors.reject("song.duplicate");
	}

}
