package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.ConflictException;
import exceptions.NotFoundException;
import io.ebean.Ebean;
import io.ebean.Transaction;
import models.*;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.StringUtils;
import repositories.*;
import requests.forms.CreateFormRequest;
import requests.forms.FilterRequest;
import requests.forms.UpdateRequest;
import responses.FilterResponse;
import responses.FormSnippet;
import services.ElasticService;
import services.FormService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormServiceImpl implements FormService
{
    private final PokemonRepository pokemonRepository;
    private final FormRepository formRepository;
    private final FormTypeMapRepository formTypeMapRepository;
    private final RegionRepository regionRepository;
    private final TypeRepository typeRepository;

    private final ElasticService elasticService;


    @Inject
    public FormServiceImpl
    (
        PokemonRepository pokemonRepository,
        FormRepository formRepository,
        FormTypeMapRepository formTypeMapRepository,
        RegionRepository regionRepository,
        TypeRepository typeRepository,
        ElasticService elasticService
    )
    {
        this.pokemonRepository = pokemonRepository;
        this.formRepository = formRepository;
        this.formTypeMapRepository = formTypeMapRepository;
        this.regionRepository = regionRepository;
        this.typeRepository = typeRepository;

        this.elasticService = elasticService;
    }

    public FormSnippet formSnippet(Form form, List<Type> types)
    {
        if(null == types)
        {
            List<FormTypeMap> formTypeMaps = this.formTypeMapRepository.get(form.getId());
            types = this.typeRepository.get(formTypeMaps.stream().map(FormTypeMap::getTypeId).collect(Collectors.toList()));
        }

        FormSnippet formSnippet = new FormSnippet(form, types);

        Pokemon pokemon = this.pokemonRepository.getByNumber(form.getPokemonNumber());
        if(null == pokemon)
        {
            throw new NotFoundException("Pokemon");
        }
        formSnippet.setPokemonNumber(pokemon.getNumber());
        formSnippet.setPokemonName(pokemon.getName());
        formSnippet.setRegionId(pokemon.getRegionId());

        List<Region> allRegions = regionRepository.getAll();
        Map<Long, Region> regionMap = allRegions.stream().collect(Collectors.toMap(Region::getId, region -> region));
        formSnippet.setRegionName(regionMap.get(pokemon.getRegionId()).getName());

        return formSnippet;
    }

    @Override
    public FormSnippet add(CreateFormRequest request)
    {
        request.validate();

        Form existingForm = this.formRepository.getByNameAndNumber(request.getName(), request.getNumber());
        if(existingForm != null)
        {
            throw new ConflictException("Form");
        }

        Transaction transaction = Ebean.beginTransaction();
        try
        {
            Pokemon pokemon = this.pokemonRepository.getByNumber(request.getNumber());
            if(null == pokemon)
            {
                throw new NotFoundException("Pokemon");
            }

            Form form = new Form(request);
            form.setReleased(true);

            Form addedForm = this.formRepository.save(form);

            List<FormTypeMap> formTypeMaps = request.getTypes().stream().map(type -> {
                FormTypeMap formTypeMap = new FormTypeMap();
                formTypeMap.setFormId(addedForm.getId());
                formTypeMap.setTypeId(type);

                return formTypeMap;
            }).collect(Collectors.toList());

            this.formTypeMapRepository.save(formTypeMaps);

            transaction.commit();
            this.elasticService.index("forms", addedForm.getId().toString(), this.formSnippet(addedForm, null));

            return formSnippet(addedForm, null);
        }
        catch (Exception ex)
        {
            transaction.rollback();
            transaction.end();
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }
    }

    @Override
    public FormSnippet get(Long id)
    {
        return this.formSnippet(this.formRepository.get(id), null);
    }

    @Override
    public FormSnippet update(UpdateRequest request, Long id)
    {
        request.validate();

        Form existingForm = this.formRepository.get(id);
        if(null == existingForm)
        {
            throw new NotFoundException("Form");
        }

        boolean isUpdateRequired = false;
        if(null != request.getName() && !request.getName().equals(existingForm.getName()))
        {
            isUpdateRequired = true;
            existingForm.setName(request.getName());
        }

        if(null != request.getReleaseDate() && !request.getReleaseDate().equals(existingForm.getReleaseDate()))
        {
            isUpdateRequired = true;
            existingForm.setReleaseDate(request.getReleaseDate());
        }

        if(!StringUtils.isEmpty(request.getImageUrl()) && !request.getImageUrl().equals(existingForm.getImageUrl()))
        {
            isUpdateRequired = true;
            existingForm.setImageUrl(request.getImageUrl());
        }

        if(null != request.getIsAlolan() && !request.getIsAlolan().equals(existingForm.isAlolan()))
        {
            isUpdateRequired = true;
            existingForm.setAlolan(request.getIsAlolan());
        }

        if(null != request.getIsGalarian() && !request.getIsGalarian().equals(existingForm.isGalarian()))
        {
            isUpdateRequired = true;
            existingForm.setGalarian(request.getIsGalarian());
        }

        if(null != request.getIsHisuian() && !request.getIsHisuian().equals(existingForm.isHisuian()))
        {
            isUpdateRequired = true;
            existingForm.setHisuian(request.getIsHisuian());
        }

        if(null != request.getIsShiny() && !request.getIsShiny().equals(existingForm.isShiny()))
        {
            isUpdateRequired = true;
            existingForm.setShiny(request.getIsShiny());
        }

        if(null != request.getIsFemale() && !request.getIsFemale().equals(existingForm.isFemale()))
        {
            isUpdateRequired = true;
            existingForm.setFemale(request.getIsFemale());
        }

        if(null != request.getIsCostumed() && !request.getIsCostumed().equals(existingForm.isCostumed()))
        {
            isUpdateRequired = true;
            existingForm.setCostumed(request.getIsCostumed());
        }

        if(null != request.getTypes())
        {
            List<FormTypeMap> formTypeMaps = this.formTypeMapRepository.get(existingForm.getId());
            List<Long> existingTypeIds = formTypeMaps.stream().map(FormTypeMap::getTypeId).collect(Collectors.toList());
            if(!request.getTypes().containsAll(existingTypeIds) || !existingTypeIds.containsAll(request.getTypes()))
            {
                List<FormTypeMap> typesToDelete = formTypeMaps
                    .stream()
                    .filter(formTypeMap -> !request.getTypes().contains(formTypeMap.getTypeId()))
                    .collect(Collectors.toList());

                List<FormTypeMap> typesToAdd = request.getTypes()
                    .stream()
                    .filter(typeId -> !existingTypeIds.contains(typeId))
                    .map(typeId -> {
                        FormTypeMap formTypeMap = new FormTypeMap();
                        formTypeMap.setFormId(id);
                        formTypeMap.setTypeId(typeId);

                        return formTypeMap;
                    })
                    .collect(Collectors.toList());

                this.formTypeMapRepository.save(typesToAdd);
                this.formTypeMapRepository.remove(typesToDelete);
            }
        }

        if(isUpdateRequired)
        {
            existingForm = this.formRepository.save(existingForm);
        }

        return formSnippet(existingForm, null);
    }

    @Override
    public FilterResponse<FormSnippet> filter(FilterRequest request) {
        return this.elasticService.search(getElasticRequest(request), FormSnippet.class);
    }

    private SearchRequest getElasticRequest(FilterRequest filterRequest)
    {
        SearchRequest request = new SearchRequest("forms");

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(filterRequest.getOffset());
        builder.size(filterRequest.getCount());

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        for(Map.Entry<String, List<String>> entry: filterRequest.getFilters().entrySet())
        {
            String key = entry.getKey();
            List<String> valueList = entry.getValue();
            if (!valueList.isEmpty())
            {
                query.must(QueryBuilders.termsQuery(key, valueList));
            }
        }

        for(Map.Entry<String, List<String>> entry: filterRequest.getAndFilters().entrySet())
        {
            String key = entry.getKey();
            List<String> valueList = entry.getValue();
            if (!valueList.isEmpty())
            {
                for(String value: valueList)
                {
                    query.must(QueryBuilders.termQuery(key, value));
                }
            }
        }

        for(Map.Entry<String, Boolean> entry: filterRequest.getBooleanFilters().entrySet())
        {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            query.must(QueryBuilders.termQuery(key, value));
        }

        builder.query(query);

        Map<String, SortOrder> sortMap = filterRequest.getSortMap();
        for(Map.Entry<String, SortOrder> sortField: sortMap.entrySet())
        {
            String key = sortField.getKey();
            SortOrder order = sortField.getValue();

            String sortKey = (key + ".sort");
            builder.sort(sortKey, order);
        }

        if(!sortMap.containsKey("pokemonNumber"))
        {
            builder.sort("pokemonNumber.sort", SortOrder.ASC);
        }

        request.source(builder);
        return request;
    }
}
