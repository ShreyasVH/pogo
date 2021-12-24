package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlQuery;
import io.ebean.SqlRow;
import models.Form;
import play.db.ebean.EbeanConfig;

import models.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;
import modules.DatabaseExecutionContext;

import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;
import play.db.ebean.EbeanDynamicEvolutions;
import requests.forms.FilterRequest;
import responses.FilterResponse;

public class FormRepository
{
	private final EbeanServer db;
	private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
	private final DatabaseExecutionContext databaseExecutionContext;

	@Inject
	public FormRepository
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

	public Form save(Form form)
	{
		try
		{
			this.db.save(form);
			return form;
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}
	}

	public List<Form> getByPokemonId(Long pokemonId)
	{
		List<Form> forms;

		try
		{
			forms = this.db.find(Form.class).where().eq("pokemonId", pokemonId).orderBy("id ASC").findList();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return forms;
	}

	public Form get(Long id)
	{
		Form form;

		try
		{
			form = this.db.find(Form.class).where().eq("id", id).findOne();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return form;
	}

	public Form getByNameAndNumber(String name, Integer number)
	{
		Form form;

		try
		{
			form = this.db.find(Form.class).where().eq("name", name).eq("pokemonNumber", number).findOne();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return form;
	}

	public FilterResponse<Map<String, Object>> filter(FilterRequest filterRequest)
	{
		FilterResponse<Map<String, Object>> response = new FilterResponse<>();
		response.setOffset(filterRequest.getOffset());
		List<Map<String, Object>> forms = new ArrayList<>();

		String query = "select f.name as formName, f.pokemon_number as pokemonNumber, p.name as pokemonName , f.image_url as imageUrl from forms f " +
				"inner join pokemons p on p.number = f.pokemon_number";

		String countQuery = "select count(distinct f.id) as count from forms f " +
				"inner join pokemons p on p.number = f.pokemon_number";

		//where
		List<String> whereQueryParts = new ArrayList<>();
		for(Map.Entry<String, List<String>> entry: filterRequest.getFilters().entrySet())
		{
			String field = entry.getKey();
			List<String> valueList = entry.getValue();

			String fieldNameWithTablePrefix = getFieldNameWithTablePrefix(field);
			if(!fieldNameWithTablePrefix.isEmpty() && !valueList.isEmpty())
			{
				whereQueryParts.add(fieldNameWithTablePrefix + " in (" + String.join(", ", valueList) + ")");
			}
		}

//		for(Map.Entry<String, List<String>> entry: filterRequest.getFilters().entrySet())
//		{
//			String field = entry.getKey();
//			List<String> valueList = entry.getValue();
//
//			String fieldNameWithTablePrefix = getFieldNameWithTablePrefix(field);
//			if(!fieldNameWithTablePrefix.isEmpty() && !valueList.isEmpty())
//			{
//				whereQueryParts.add(fieldNameWithTablePrefix + " in (" + String.join(", ", valueList) + ")");
//			}
//		}

		if(!whereQueryParts.isEmpty())
		{
			query += " where " + String.join(" and ", whereQueryParts);
			countQuery += " where " + String.join(" and ", whereQueryParts);
		}

		//sort
		List<String> sortList = new ArrayList<>();
		for(Map.Entry<String, String> entry: filterRequest.getSortMap().entrySet())
		{
			String field = entry.getKey();
			String value = entry.getValue();

			String sortFieldName = getFieldNameForDisplay(field);
			if(!sortFieldName.isEmpty())
			{
				sortList.add(sortFieldName + " " + value);
			}
		}
		if(sortList.isEmpty())
		{
			sortList.add(getFieldNameForDisplay("number") + " asc");
		}
		query += " order by " + String.join(", ", sortList);

		//offset limit
		query += " limit " + Integer.min(30, filterRequest.getCount()) + " offset " + filterRequest.getOffset();

		try
		{
			SqlQuery sqlCountQuery = this.db.createSqlQuery(countQuery);
			List<SqlRow> countResult = sqlCountQuery.findList();
			response.setTotalCount(countResult.get(0).getInteger("count"));

			SqlQuery sqlQuery = this.db.createSqlQuery(query);
			List<SqlRow> result = sqlQuery.findList();

			for(SqlRow row: result)
			{
				Map<String, Object> entry = new HashMap<>();
				entry.put("formName", row.getString("formName"));
				entry.put("pokemonName", row.getString("pokemonName"));
				entry.put("pokemonNumber", row.getInteger("pokemonNumber"));
				entry.put("imageUrl", row.getString("imageUrl"));

				forms.add(entry);
			}
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		response.setList(forms);

		return response;
	}

	public String getFieldNameWithTablePrefix(String field)
	{
		String fieldName = "";

		switch(field)
		{
			case "number":
				fieldName = "p.number";
				break;
			case "type":
				fieldName = "t.id";
				break;
		}

		return fieldName;
	}

	public String getFieldNameForDisplay(String field)
	{
		String fieldName = "";

		switch(field)
		{
			case "number":
				fieldName = "number";
				break;
			case "type":
				fieldName = "type";
				break;
		}

		return fieldName;
	}
}