package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import requests.forms.CreateFormRequest;
import requests.forms.FilterRequest;
import requests.forms.UpdateRequest;
import services.FormService;
import services.PokemonService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FormController extends BaseController
{
	private final FormService formService;
	private final HttpExecutionContext httpExecutionContext;

	@Inject
	public FormController
	(
		FormService formService,
		HttpExecutionContext httpExecutionContext
	)
	{
		this.formService = formService;
		this.httpExecutionContext = httpExecutionContext;
	}

	public CompletionStage<Result> create(Http.Request request)
	{
		return CompletableFuture.supplyAsync(() -> {
			CreateFormRequest createRequest;
			try
			{
				createRequest = Utils.convertObject(request.body().asJson(), CreateFormRequest.class);
			}
			catch(Exception ex)
			{
				throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
			}

			return this.formService.add(createRequest);
		}, this.httpExecutionContext.current()).thenApplyAsync(match -> ok(Json.toJson(match)), this.httpExecutionContext.current());
	}

	public CompletionStage<Result> get(Long id)
	{
		return CompletableFuture
			.supplyAsync(() -> this.formService.get(id), this.httpExecutionContext.current())
			.thenApplyAsync(match -> ok(Json.toJson(match)), this.httpExecutionContext.current());
	}

	public CompletionStage<Result> update(Long id, Http.Request request)
	{
		return CompletableFuture.supplyAsync(() -> {
			UpdateRequest updateRequest;
			try
			{
				updateRequest = Utils.convertObject(request.body().asJson(), UpdateRequest.class);
			}
			catch(Exception ex)
			{
				throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
			}

			return this.formService.update(updateRequest, id);
		}, this.httpExecutionContext.current()).thenApplyAsync(match -> ok(Json.toJson(match)), this.httpExecutionContext.current());
	}

	public CompletionStage<Result> filter(Http.Request request)
	{
		return CompletableFuture.supplyAsync(() -> {
			FilterRequest filterRequest = null;
			try
			{
				filterRequest = Utils.convertObject(request.body().asJson(), FilterRequest.class);
			}
			catch(Exception ex)
			{
				throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
			}

			return this.formService.filter(filterRequest);
		}, this.httpExecutionContext.current()).thenApplyAsync(response -> ok(Json.toJson(response)), this.httpExecutionContext.current());
	}
}