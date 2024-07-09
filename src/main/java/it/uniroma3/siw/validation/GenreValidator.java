package it.uniroma3.siw.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Genre;
import it.uniroma3.siw.service.GenreService;

@Component
public class GenreValidator implements Validator{
	
	@Autowired GenreService genreService;

	@Override
	public boolean supports(Class<?> clazz) {
		return Genre.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Genre genre = (Genre) target;
		if(this.genreService.exsistsByName(genre))
			errors.reject("genre.duplicate");
	}
	
	
	
}
