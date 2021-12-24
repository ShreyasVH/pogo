package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import requests.events.CreateRequest;
import requests.forms.CreateFormRequest;
import requests.forms.FilterRequest;
import requests.forms.UpdateRequest;
import services.EventService;
import services.FormService;
import services.PokemonService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class EventController extends BaseController
{
    private final EventService eventService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public EventController
        (
            EventService eventService,
            HttpExecutionContext httpExecutionContext
        )
    {
        this.eventService = eventService;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> create(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            CreateRequest createRequest;
            try
            {
                createRequest = Utils.convertObject(request.body().asJson(), CreateRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.eventService.add(createRequest);
        }, this.httpExecutionContext.current()).thenApplyAsync(match -> ok(Json.toJson(match)), this.httpExecutionContext.current());
    }
}