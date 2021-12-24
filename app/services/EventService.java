package services;

import models.Event;
import requests.events.CreateRequest;
import requests.events.UpdateRequest;
import responses.EventSnippet;

public interface EventService
{
    Event add(CreateRequest request);

    EventSnippet get(Long id);

    Event update(UpdateRequest request, Long id);
}
