package services.impl;

import com.google.inject.Inject;
import exceptions.ConflictException;
import exceptions.NotFoundException;
import models.Form;
import models.Pokemon;
import org.springframework.util.StringUtils;
import repositories.FormRepository;
import repositories.PokemonRepository;
import requests.forms.CreateFormRequest;
import requests.forms.FilterRequest;
import requests.forms.UpdateRequest;
import responses.FilterResponse;
import responses.FormSnippet;
import services.FormService;

import java.util.List;
import java.util.stream.Collectors;

public class FormServiceImpl implements FormService
{
    private final PokemonRepository pokemonRepository;
    private final FormRepository formRepository;


    @Inject
    public FormServiceImpl
    (
        PokemonRepository pokemonRepository,
        FormRepository formRepository
    )
    {
        this.pokemonRepository = pokemonRepository;
        this.formRepository = formRepository;
    }

    public FormSnippet formSnippet(Form form)
    {
        FormSnippet formSnippet = new FormSnippet(form);

        Pokemon pokemon = this.pokemonRepository.getByNumber(form.getPokemonNumber());
        if(null == pokemon)
        {
            throw new NotFoundException("Pokemon");
        }
        formSnippet.setPokemon(pokemon);

        return formSnippet;
    }

    @Override
    public Form add(CreateFormRequest request)
    {
        request.validate();

        Form existingForm = this.formRepository.getByNameAndNumber(request.getName(), request.getNumber());
        if(existingForm != null)
        {
            throw new ConflictException("Form");
        }

        Pokemon pokemon = this.pokemonRepository.getByNumber(request.getNumber());
        if(null == pokemon)
        {
            throw new NotFoundException("Pokemon");
        }

        Form form = new Form(request);

        return this.formRepository.save(form);
    }

    @Override
    public FormSnippet get(Long id)
    {
        return this.formSnippet(this.formRepository.get(id));
    }

    @Override
    public Form update(UpdateRequest request, Long id)
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

        if(isUpdateRequired)
        {
            existingForm = this.formRepository.save(existingForm);
        }

        return existingForm;
    }

    @Override
    public FilterResponse filter(FilterRequest request) {
        return this.formRepository.filter(request);
    }
}
