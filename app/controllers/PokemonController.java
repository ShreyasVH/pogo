package controllers;

import enums.ErrorCode;
import exceptions.BadRequestException;
import play.mvc.Http;
import play.mvc.Result;
import play.libs.Json;

import com.google.inject.Inject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import play.libs.concurrent.HttpExecutionContext;

import requests.CreatePokemonRequest;
import requests.UpdatePokemonRequest;
import services.PokemonService;
import utils.Utils;

public class PokemonController extends BaseController
{
	private final PokemonService pokemonService;
	private final HttpExecutionContext httpExecutionContext;

	@Inject
	public PokemonController
	(
		PokemonService pokemonService,
		HttpExecutionContext httpExecutionContext
	)
	{
		this.pokemonService = pokemonService;
		this.httpExecutionContext = httpExecutionContext;
	}

	public CompletionStage<Result> create(Http.Request request)
	{
		return CompletableFuture.supplyAsync(() -> {
			CreatePokemonRequest createRequest;
			try
			{
				createRequest = Utils.convertObject(request.body().asJson(), CreatePokemonRequest.class);
			}
			catch(Exception ex)
			{
				throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
			}

			return this.pokemonService.create(createRequest);
		}, this.httpExecutionContext.current()).thenApplyAsync(pokemon -> ok(Json.toJson(pokemon)), this.httpExecutionContext.current());
	}

	public CompletionStage<Result> update(Integer number, Http.Request request)
	{
		return CompletableFuture.supplyAsync(() -> {
			UpdatePokemonRequest updatePokemonRequest;
			try
			{
				updatePokemonRequest = Utils.convertObject(request.body().asJson(), UpdatePokemonRequest.class);
			}
			catch(Exception ex)
			{
				throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
			}

			return this.pokemonService.update(updatePokemonRequest, number);
		}, this.httpExecutionContext.current()).thenApplyAsync(pokemon -> ok(Json.toJson(pokemon)), this.httpExecutionContext.current());
	}

	public CompletionStage<Result> get(Long id)
	{
		return CompletableFuture.supplyAsync(() -> this.pokemonService.get(id)).thenApplyAsync(pokemonSnippet -> ok(Json.toJson(pokemonSnippet)), this.httpExecutionContext.current());
	}

	public CompletionStage<Result> getByNumber(Integer number)
	{
		return CompletableFuture.supplyAsync(() -> this.pokemonService.getByNumber(number)).thenApplyAsync(pokemonSnippet -> ok(Json.toJson(pokemonSnippet)), this.httpExecutionContext.current());
	}

	public CompletionStage<Result> getAll()
	{
		return CompletableFuture.supplyAsync(this.pokemonService::getAll).thenApplyAsync(pokemons -> ok(Json.toJson(pokemons)), this.httpExecutionContext.current());
	}
}