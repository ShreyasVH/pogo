package services.impl;

import com.google.inject.Inject;

import java.util.stream.Collectors;

import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import io.ebean.Ebean;
import io.ebean.Transaction;
import models.Pokemon;

import models.PokemonCandyMap;
import models.PokemonItemMap;
import models.PokemonTypeMap;
import repositories.*;

import requests.CreatePokemonRequest;
import services.PokemonService;

public class PokemonServiceImpl implements PokemonService
{
	private final PokemonRepository pokemonRepository;
	private final PokemonCandyMapRepository pokemonCandyMapRepository;
	private final PokemonItemMapRepository pokemonItemMapRepository;
	private final PokemonTypeMapRepository pokemonTypeMapRepository;
	private final TypeRepository typeRepository;
	
	@Inject
	public PokemonServiceImpl
	(
		PokemonRepository pokemonRepository,
		PokemonCandyMapRepository pokemonCandyMapRepository,
		PokemonItemMapRepository pokemonItemMapRepository,
		PokemonTypeMapRepository pokemonTypeMapRepository,
		TypeRepository typeRepository
	)
	{
		this.pokemonRepository = pokemonRepository;
		this.pokemonCandyMapRepository = pokemonCandyMapRepository;
		this.pokemonItemMapRepository = pokemonItemMapRepository;
		this.pokemonTypeMapRepository = pokemonTypeMapRepository;
		this.typeRepository = typeRepository;

	}

	public Pokemon create(CreatePokemonRequest request)
	{
		request.validate();

		Transaction transaction = Ebean.beginTransaction();
		try
		{
			Pokemon addedPokemon = pokemonRepository.save(new Pokemon(request));

			this.pokemonTypeMapRepository.save(request.getTypes().stream().map(type -> {
				PokemonTypeMap map = new PokemonTypeMap();
				map.setPokemonId(addedPokemon.getId());
				map.setTypeId(type);

				return map;
			}).collect(Collectors.toList()));

			this.pokemonItemMapRepository.save(request.getItems().stream().map(item -> {
				PokemonItemMap map = new PokemonItemMap();
				map.setPokemonId(addedPokemon.getId());
				map.setItemId(item);

				return map;
			}).collect(Collectors.toList()));

			Long candyPokemonId = null;
			if(null != request.getCandyPokemonNumber())
			{
				Pokemon candyPokemon = this.pokemonRepository.get(request.getCandyPokemonNumber());
				if(null == candyPokemon)
				{
					throw new NotFoundException(ErrorCode.NOT_FOUND.getCode(), String.format(ErrorCode.NOT_FOUND.getDescription(), "Candy Pokemon"));
				}
				candyPokemonId = candyPokemon.getId();
			}
			else
			{
				candyPokemonId = addedPokemon.getId();
			}

			PokemonCandyMap candyMap = new PokemonCandyMap();
			candyMap.setPokemonId(addedPokemon.getId());
			candyMap.setCandyPokemonId(candyPokemonId);
			pokemonCandyMapRepository.save(candyMap);

			transaction.commit();
			transaction.end();
			return addedPokemon;
		}
		catch(Exception ex)
		{
			transaction.rollback();
			transaction.end();
			throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
		}
	}
}