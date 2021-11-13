package controllers;

import play.mvc.Result;
import play.libs.Json;

import com.google.inject.Inject;

import java.util.concurrent.CompletionStage;

import play.libs.concurrent.HttpExecutionContext;

import services.TypeService;

public class TypeController extends BaseController
{
	private final TypeService typeService;
	private final HttpExecutionContext httpExecutionContext;

	@Inject
	public TypeController
	(
		TypeService typeService,
		HttpExecutionContext httpExecutionContext
	)
	{
		this.typeService = typeService;
		this.httpExecutionContext = httpExecutionContext;
	}

	public CompletionStage<Result> getAll()
	{
		return this.typeService.getAll().thenApplyAsync(list -> ok(Json.toJson(list)), this.httpExecutionContext.current());
	}
}