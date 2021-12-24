package services.impl;

import com.google.inject.Inject;

import java.util.List;

import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.ConflictException;
import exceptions.NotFoundException;
import io.ebean.Ebean;
import io.ebean.Transaction;
import models.*;

import org.springframework.util.StringUtils;
import repositories.*;

import requests.UpdatePokemonRequest;
import requests.forms.CreateFormRequest;
import requests.CreatePokemonRequest;
import responses.FormSnippet;
import responses.PokemonSnippet;
import services.PokemonService;
import services.RegionService;

public class PokemonServiceImpl implements PokemonService
{
	private final PokemonRepository pokemonRepository;
	private final PokemonCandyMapRepository pokemonCandyMapRepository;
	private final FormRepository formRepository;

	private final RegionService regionService;
	
	@Inject
	public PokemonServiceImpl
	(
		PokemonRepository pokemonRepository,
		PokemonCandyMapRepository pokemonCandyMapRepository,
		FormRepository formRepository,
		RegionService regionService
	)
	{
		this.pokemonRepository = pokemonRepository;
		this.pokemonCandyMapRepository = pokemonCandyMapRepository;
		this.formRepository = formRepository;

		this.regionService = regionService;
	}

	public PokemonSnippet pokemonSnippet(Pokemon pokemon)
	{
		PokemonSnippet pokemonSnippet = new PokemonSnippet(pokemon);

		Region region = this.regionService.get(pokemon.getRegionId());

		if(null == region)
		{
			throw new NotFoundException("Region");
		}

		pokemonSnippet.setRegion(region);
		pokemonSnippet.setCandiesToEvolve(pokemon.getCandiesToEvolve());

		PokemonCandyMap pokemonCandyMap = this.pokemonCandyMapRepository.getByNumber(pokemon.getNumber());
		if(null == pokemonCandyMap)
		{
			throw new NotFoundException("Candy Pokemon");
		}
		Pokemon candyPokemon = this.pokemonRepository.getByNumber(pokemonCandyMap.getCandyPokemonNumber());

		if(null == candyPokemon)
		{
			throw new NotFoundException("Candy Pokemon");
		}
		pokemonSnippet.setCandyPokemon(candyPokemon);

		return pokemonSnippet;
	}

	public PokemonSnippet get(Long id)
	{
		Pokemon pokemon = this.pokemonRepository.get(id);

		if(null == pokemon)
		{
			throw new NotFoundException("Pokemon");
		}

		return pokemonSnippet(pokemon);
	}

	public PokemonSnippet getByNumber(Integer number)
	{
		Pokemon pokemon = this.pokemonRepository.getByNumber(number);

		if(null == pokemon)
		{
			throw new NotFoundException("Pokemon");
		}

		return pokemonSnippet(pokemon);
	}

    public Pokemon create(CreatePokemonRequest request)
	{
		request.validate();

		Pokemon existingPokemon = pokemonRepository.getByName(request.getName());
		if(null != existingPokemon)
		{
			throw new ConflictException("Pokemon");
		}

		Transaction transaction = Ebean.beginTransaction();
		try
		{
			Pokemon addedPokemon = pokemonRepository.save(new Pokemon(request));

			Integer candyPokemonNumber = null;
			if(null != request.getCandyPokemonNumber())
			{
				candyPokemonNumber = request.getCandyPokemonNumber();
			}
			else
			{
				candyPokemonNumber = request.getNumber();
			}

			PokemonCandyMap candyMap = new PokemonCandyMap();
			candyMap.setPokemonNumber(addedPokemon.getNumber());
			candyMap.setCandyPokemonNumber(candyPokemonNumber);
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

	@Override
	public Pokemon update(UpdatePokemonRequest request, Integer number)
	{
		request.validate();

		Pokemon existingPokemon = pokemonRepository.getByNumber(number);
		if(null == existingPokemon)
		{
			throw new NotFoundException("Pokemon");
		}

		boolean isUpdateRequired = false;

		Transaction transaction = Ebean.beginTransaction();
		try
		{
			if(!StringUtils.isEmpty(request.getName()) && !request.getName().equals(existingPokemon.getName()))
			{
				isUpdateRequired = true;
				existingPokemon.setName(request.getName());
			}

			if((request.getRegionId() != null) && !request.getRegionId().equals(existingPokemon.getRegionId()))
			{
				Region region = this.regionService.get(request.getRegionId());
				if(region == null)
				{
					throw new NotFoundException("Region");
				}

				isUpdateRequired = true;
				existingPokemon.setRegionId(request.getRegionId());
			}

			if(request.getCandyPokemonNumber() != null)
			{
				PokemonCandyMap pokemonCandyMap = this.pokemonCandyMapRepository.getByNumber(number);
				if(!request.getCandyPokemonNumber().equals(pokemonCandyMap.getCandyPokemonNumber()))
				{
					isUpdateRequired = true;
					pokemonCandyMap.setCandyPokemonNumber(request.getCandyPokemonNumber());

					this.pokemonCandyMapRepository.save(pokemonCandyMap);
				}
			}

			if(isUpdateRequired)
			{
				existingPokemon = this.pokemonRepository.save(existingPokemon);
				transaction.commit();
			}

			transaction.end();

			return existingPokemon;
		}
		catch(Exception ex)
		{
			transaction.rollback();
			transaction.end();
			throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
		}
	}

    @Override
    public List<Pokemon> getAll() {
		return this.pokemonRepository.getAll();
    }

//	@Override
//	public List<Form> filter(FilterRequest request)
//	{
//
//	}


}