package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import play.db.ebean.EbeanConfig;

import models.Region;
import java.util.List;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;
import play.db.ebean.EbeanDynamicEvolutions;

public class RegionRepository
{
	private final EbeanServer db;
	private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
	private final DatabaseExecutionContext databaseExecutionContext;

	@Inject
	public RegionRepository
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

	public List<Region> getAll()
	{
		List<Region> regions;

		try
		{
			regions = this.db.find(Region.class).orderBy("id ASC").findList();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return regions;
	}

	public Region get(Long id)
	{
		Region region = null;

		try
		{
			region = this.db.find(Region.class).where().eq("id", id).findOne();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return region;
	}
}