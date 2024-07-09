package it.uniroma3.siw.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.service.AlbumService;

@Component
public class AlbumValidator implements Validator {
	
	@Autowired AlbumService albumService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Album.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Album album = (Album) target;
		if(this.albumService.existsByNameAndArtist(album.getName(), album.getArtist()))
			errors.reject("album.duplicate");
	}

}
