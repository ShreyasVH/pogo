package controllers;

import play.mvc.Result;
import play.libs.Json;

import com.google.inject.Inject;

import java.util.concurrent.CompletionStage;

import play.libs.concurrent.HttpExecutionContext;

import services.ItemService;

public class ItemController extends BaseController
{
	private final ItemService itemService;
	private final HttpExecutionContext httpExecutionContext;

	@Inject
	public ItemController
	(
		ItemService itemService,
		HttpExecutionContext httpExecutionContext
	)
	{
		this.itemService = itemService;
		this.httpExecutionContext = httpExecutionContext;
	}

	public CompletionStage<Result> getAll()
	{
		return this.itemService.getAll().thenApplyAsync(list -> ok(Json.toJson(list)), this.httpExecutionContext.current());
	}
}