package controllers;

import play.mvc.Result;
import play.libs.Json;

import com.google.inject.Inject;

import java.util.concurrent.CompletionStage;

import play.libs.concurrent.HttpExecutionContext;

import services.RegionService;

public class RegionController extends BaseController
{
	private final RegionService regionService;
	private final HttpExecutionContext httpExecutionContext;

	@Inject
	public RegionController
	(
		RegionService regionService,
		HttpExecutionContext httpExecutionContext
	)
	{
		this.regionService = regionService;
		this.httpExecutionContext = httpExecutionContext;
	}

	public CompletionStage<Result> getAll()
	{
		return this.regionService.getAll().thenApplyAsync(list -> ok(Json.toJson(list)), this.httpExecutionContext.current());
	}
}