package services.impl;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import exceptions.ConflictException;
import exceptions.NotFoundException;
import io.ebean.Ebean;
import io.ebean.Transaction;
import models.Event;
import models.EventForm;
import models.Form;
import repositories.EventFormRepository;
import repositories.EventRepository;
import repositories.FormRepository;
import requests.events.CreateRequest;
import responses.EventSnippet;
import services.EventService;

import java.util.List;
import java.util.stream.Collectors;

public class EventServiceImpl implements EventService
{
    private EventRepository eventRepository;
    private EventFormRepository eventFormRepository;
    private FormRepository formRepository;

    @Inject
    public EventServiceImpl
        (
            EventRepository eventRepository,
            EventFormRepository eventFormRepository,
            FormRepository formRepository
        )
    {
        this.eventRepository = eventRepository;
        this.eventFormRepository = eventFormRepository;
        this.formRepository = formRepository;
    }

    public EventSnippet eventSnippet(Event event)
    {
        EventSnippet eventSnippet = new EventSnippet(event);

        List<EventForm> forms = this.eventFormRepository.getByEventId(event.getId());

        eventSnippet.setForms(this.formRepository.get(
            forms
                .stream()
                .map(EventForm::getFormId)
                .collect(Collectors.toList())
        ));

        return eventSnippet;
    }

    @Override
    public Event add(CreateRequest request)
    {
        request.validate();

        Event existingEvent = this.eventRepository.getByName(request.getName());
        if(null != existingEvent)
        {
            throw new ConflictException("Event");
        }

        Transaction transaction = Ebean.beginTransaction();
        try
        {
            Event event = new Event(request);
            event = this.eventRepository.save(event);

            Event finalEvent = event;
            this.eventFormRepository.save(
                request.getForms()
                    .stream()
                    .map(formId -> {
                        Form existingForm = this.formRepository.get(formId);
                        if(existingForm == null)
                        {
                            throw new NotFoundException("Form " + formId);
                        }

                        EventForm eventForm = new EventForm();
                        eventForm.setEventId(finalEvent.getId());
                        eventForm.setFormId(formId);

                        return eventForm;
                    })
                    .collect(Collectors.toList())
            );

            transaction.commit();
            transaction.end();
            return event;
        }
        catch(Exception ex)
        {
            transaction.rollback();
            transaction.end();
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
        }
    }

    @Override
    public EventSnippet get(Long id)
    {
        return this.eventSnippet(this.eventRepository.get(id));
    }
}
