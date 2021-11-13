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
		}, this.httpExecutionContext.current()).thenApplyAsync(match -> ok(Json.toJson(match)), this.httpExecutionContext.current());
	}
}