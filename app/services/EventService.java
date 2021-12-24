package services;

import models.Event;
import requests.events.CreateRequest;
import responses.EventSnippet;

public interface EventService
{
    Event add(CreateRequest request);

    EventSnippet get(Long id);
}
