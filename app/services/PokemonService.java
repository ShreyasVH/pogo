package services;

import models.Form;
import models.Pokemon;
import requests.UpdatePokemonRequest;
import requests.forms.CreateFormRequest;
import requests.CreatePokemonRequest;
import requests.forms.FilterRequest;
import responses.PokemonSnippet;

import java.util.List;

public interface PokemonService
{
	Pokemon create(CreatePokemonRequest request);

	Pokemon update(UpdatePokemonRequest request, Integer number);

	PokemonSnippet get(Long id);

	PokemonSnippet getByNumber(Integer number);

	List<Pokemon> getAll();

//	List<Form> filter(FilterRequest request);

}