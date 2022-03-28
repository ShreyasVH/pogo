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

	public List<Form> get(List<Long> ids)
	{
		List<Form> forms = new ArrayList<>();

		try
		{
			forms = this.db.find(Form.class).where().in("id", ids).findList();
		}
		catch(Exception ex)
		{
			String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
			throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
		}

		return forms;
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

		String query = "select f.name as formName, f.id as formId, f.pokemon_number as pokemonNumber, p.name as pokemonName , f.image_url as imageUrl, f.is_costumed as isCostumed, f.is_shiny as isShiny, f.is_female as isFemale, f.is_alolan as isAlolan, f.is_galarian as isGalarian, f.is_hisuian as isHisuian from forms f " +
				"inner join pokemons p on p.number = f.pokemon_number";

		String countQuery = "select count(distinct f.id) as count from forms f " +
				"inner join pokemons p on p.number = f.pokemon_number";

		//where
		List<String> whereQueryParts = new ArrayList<>();

		whereQueryParts.add(getFieldNameWithTablePrefix("is_released") + " = 1");

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

		for(Map.Entry<String, Boolean> entry: filterRequest.getBooleanFilters().entrySet())
		{
			String field = entry.getKey();
			Boolean value = entry.getValue();

			String fieldNameWithTablePrefix = getFieldNameWithTablePrefix(field);
			if(!fieldNameWithTablePrefix.isEmpty() && null != value)
			{
				whereQueryParts.add(fieldNameWithTablePrefix + " = " + value);
			}
		}

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
			sortList.add(getFieldNameForDisplay("formId") + " asc");
		}
		query += " order by " + String.join(", ", sortList);

		//offset limit
		query += " limit " + Integer.min(100, filterRequest.getCount()) + " offset " + filterRequest.getOffset();

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
				entry.put("formId", row.getLong("formId"));
				entry.put("formName", row.getString("formName"));
				entry.put("pokemonName", row.getString("pokemonName"));
				entry.put("pokemonNumber", row.getInteger("pokemonNumber"));
				entry.put("imageUrl", row.getString("imageUrl"));
				entry.put("isCostumed", row.getBoolean("isCostumed"));
				entry.put("isFemale", row.getBoolean("isFemale"));
				entry.put("isShiny", row.getBoolean("isShiny"));
				entry.put("isAlolan", row.getBoolean("isAlolan"));
				entry.put("isGalarian", row.getBoolean("isGalarian"));
				entry.put("isHisuian", row.getBoolean("isHisuian"));

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
			case "is_released":
				fieldName = "f.is_released";
				break;
			case "is_alolan":
				fieldName = "f.is_alolan";
				break;
			case "is_galarian":
				fieldName = "f.is_galarian";
				break;
			case "is_hisuian":
				fieldName = "f.is_hisuian";
				break;
			case "is_female":
				fieldName = "f.is_female";
				break;
			case "is_shiny":
				fieldName = "f.is_shiny";
				break;
			case "is_costumed":
				fieldName = "f.is_costumed";
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
			case "formId":
				fieldName = "formId";
		}

		return fieldName;
	}
}