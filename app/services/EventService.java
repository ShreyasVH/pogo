package services;

import models.Event;
import requests.events.CreateRequest;
import requests.events.UpdateRequest;
import requests.forms.FilterRequest;
import responses.EventSnippet;
import responses.FilterResponse;

import java.util.Map;

public interface EventService
{
    Event add(CreateRequest request);

    EventSnippet get(Long id);

    Event update(UpdateRequest request, Long id);

    FilterResponse<Map<String, Object>> filter(FilterRequest filterRequest);
}
