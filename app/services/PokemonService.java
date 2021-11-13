package services;

import models.Pokemon;
import requests.CreatePokemonRequest;

public interface PokemonService
{
	Pokemon create(CreatePokemonRequest request);
}