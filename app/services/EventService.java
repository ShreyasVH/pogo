package services;

import models.Event;
import requests.events.CreateRequest;

public interface EventService
{
    Event add(CreateRequest request);
}
