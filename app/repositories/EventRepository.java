package repositories;

import enums.ErrorCode;
import exceptions.DBInteractionException;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.SqlQuery;
import io.ebean.SqlRow;
import models.Event;
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

public class EventRepository
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public EventRepository
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

    public Event save(Event event)
    {
        try
        {
            this.db.save(event);
            return event;
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
    }

    public Event getByName(String name)
    {
        Event event = null;
        try
        {
            event = this.db.find(Event.class).where().eq("name", name).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return event;
    }

    public Event get(Long id)
    {
        Event event = null;
        try
        {
            event = this.db.find(Event.class).where().eq("id", id).findOne();
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }
        return event;
    }

    public FilterResponse<Map<String, Object>> filter(FilterRequest filterRequest)
    {
        FilterResponse<Map<String, Object>> response = new FilterResponse<>();
        response.setOffset(filterRequest.getOffset());
        List<Map<String, Object>> events = new ArrayList<>();

        String query = "SELECT id, name, start_time AS startTime, end_time AS endTime FROM `events`";

        String countQuery = "SELECT COUNT(*) as count FROM `events`";

        //where
        List<String> whereQueryParts = new ArrayList<>();

        for(Map.Entry<String, List<String>> entry: filterRequest.getFilters().entrySet())
        {
            String field = entry.getKey();
            List<String> valueList = entry.getValue();

            String fieldNameWithTablePrefix = getFieldNameForDisplay(field);
            if(!fieldNameWithTablePrefix.isEmpty() && !valueList.isEmpty())
            {
                whereQueryParts.add(fieldNameWithTablePrefix + " in (" + String.join(", ", valueList) + ")");
            }
        }

        for(Map.Entry<String, Map<String, String>> entry: filterRequest.getRangeFilters().entrySet())
        {
            String field = entry.getKey();
            Map<String, String> rangeValues = entry.getValue();

            String fieldNameWithTablePrefix = getFieldNameForDisplay(field);
            if(!fieldNameWithTablePrefix.isEmpty() && !rangeValues.isEmpty())
            {
                if(rangeValues.containsKey("from"))
                {
                    whereQueryParts.add(fieldNameWithTablePrefix + " >= " +  rangeValues.get("from"));
                }
                if(rangeValues.containsKey("to"))
                {
                    whereQueryParts.add(fieldNameWithTablePrefix + " <= " +  rangeValues.get("to"));
                }

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
            sortList.add(getFieldNameForDisplay("start_time") + " desc");
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
                entry.put("id", row.getLong("id"));
                entry.put("name", row.getString("name"));
                entry.put("startTime", row.getLong("startTime"));
                entry.put("endTime", row.getLong("endTime"));

                events.add(entry);
            }
        }
        catch(Exception ex)
        {
            String message = ErrorCode.DB_INTERACTION_FAILED.getDescription() + ". Exception: " + ex;
            throw new DBInteractionException(ErrorCode.DB_INTERACTION_FAILED.getCode(), message);
        }

        response.setList(events);

        return response;
    }

    private String getFieldNameForDisplay(String name)
    {
        String fieldName = name;

        switch(name)
        {
            case "startTime":
                fieldName = "start_time";
                break;
            case "endTime":
                fieldName = "end_time";
                break;
        }
        return fieldName;
    }
}