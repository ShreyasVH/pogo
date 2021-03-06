package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import play.db.ebean.EbeanConfig;

import models.FormTypeMap;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import play.db.ebean.EbeanDynamicEvolutions;

public class FormTypeMapRepository
{
	private final EbeanServer db;
	private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
	private final DatabaseExecutionContext databaseExecutionContext;

	@Inject
	public FormTypeMapRepository
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

	public void save(List<FormTypeMap> list)
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

	public List<FormTypeMap> get(Long formId)
	{
		List<FormTypeMap> list = new ArrayList<>();

		try
		{
			list = this.db.find(FormTypeMap.class).where().eq("formId", formId).findList();
		}
		catch (Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return list;
	}
}