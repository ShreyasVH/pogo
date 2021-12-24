package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import play.db.ebean.EbeanConfig;

import models.Pokemon;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import play.db.ebean.EbeanDynamicEvolutions;

import java.util.ArrayList;
import java.util.List;

public class PokemonRepository
{
	private final EbeanServer db;
	private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
	private final DatabaseExecutionContext databaseExecutionContext;

	@Inject
	public PokemonRepository
	(
		EbeanConfig ebeanConfig,
		EbeanDynamicEvolutions ebeanDynamicEvolutions,
		DatabaseExecutionContext databaseExecutionContext
	)
	{
		this.ebeanDynamicEvolutions = ebeanDynamicEvolutions;
		this.db = Ebean.getServer(ebeanConfig.defaultServer());
		this.databaseExecutionContext = databaseExecutionContext;
	}

	public Pokemon save(Pokemon pokemon)
	{
		try
		{
			this.db.save(pokemon);
			return pokemon;
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
	}

	public Pokemon get(Long id)
	{
		Pokemon pokemon = null;

		try
		{
			pokemon = this.db.find(Pokemon.class).where().eq("id", id).findOne();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return pokemon;
	}

	public Pokemon getByNumber(Integer number)
	{
		Pokemon pokemon = null;

		try
		{
			pokemon = this.db.find(Pokemon.class).where().eq("number", number).findOne();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return pokemon;
	}

	public Pokemon getByName(String name)
	{
		Pokemon pokemon = null;

		try
		{
			pokemon = this.db.find(Pokemon.class).where().eq("name", name).findOne();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return pokemon;
	}

	public List<Pokemon> getAll()
	{
		List<Pokemon> pokemons = new ArrayList<>();

		try
		{
			pokemons = this.db.find(Pokemon.class).orderBy("number ASC").findList();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return pokemons;
	}
}