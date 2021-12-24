package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.PokemonItemMap;
import models.PokemonTypeMap;
import play.db.ebean.EbeanConfig;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import play.db.ebean.EbeanDynamicEvolutions;

public class PokemonItemMapRepository
{
	private final EbeanServer db;
	private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
	private final DatabaseExecutionContext databaseExecutionContext;

	@Inject
	public PokemonItemMapRepository
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

	public void save(List<PokemonItemMap> list)
	{
		if(!list.isEmpty())
		{
			try
			{
				this.db.saveAll(list);
			}
			catch (Exception ex)
			{
				String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
				throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
			}
		}
	}

	public List<PokemonItemMap> get(Long pokemonId)
	{
		List<PokemonItemMap> list = new ArrayList<>();

		try
		{
			list = this.db.find(PokemonItemMap.class).where().eq("pokemonId", pokemonId).findList();
		}
		catch (Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return list;
	}
}