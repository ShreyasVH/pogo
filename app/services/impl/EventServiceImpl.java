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
import requests.events.UpdateRequest;
import requests.forms.FilterRequest;
import responses.EventSnippet;
import responses.FilterResponse;
import services.EventService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Override
    public Event update(UpdateRequest request, Long id)
    {
        request.validate();

        Event existingEvent = this.eventRepository.get(id);
        if(null == existingEvent)
        {
            throw new NotFoundException("Event");
        }

        boolean isUpdateRequired = false;

        if(request.getName() != null && !request.getName().equals(existingEvent.getName()))
        {
            isUpdateRequired = true;
            existingEvent.setName(request.getName());
        }

        if(request.getStartTime() != null && !request.getStartTime().equals(existingEvent.getStartTime()))
        {
            isUpdateRequired = true;
            existingEvent.setStartTime(request.getStartTime());
        }

        if(request.getEndTime() != null && !request.getEndTime().equals(existingEvent.getEndTime()))
        {
            isUpdateRequired = true;
            existingEvent.setEndTime(request.getEndTime());
        }

        Transaction transaction = Ebean.beginTransaction();

        try
        {
            if(isUpdateRequired)
            {
                existingEvent = this.eventRepository.save(existingEvent);
            }

            if(request.getForms() != null)
            {
                List<EventForm> existingForms = this.eventFormRepository.getByEventId(id);
                List<EventForm> formsToDelete = new ArrayList<>();
                List<Long> existingFormIds = existingForms.stream().map(form -> {
                    if(!request.getForms().contains(form.getFormId()))
                    {
                        formsToDelete.add(form);
                    }

                    return form.getFormId();
                })
                .collect(Collectors.toList());

                this.eventFormRepository.delete(formsToDelete);

                List<EventForm> formsToAdd = new ArrayList<>();
                for(Long formId: request.getForms())
                {
                    if(!existingFormIds.contains(formId))
                    {
                        EventForm form = new EventForm();
                        form.setEventId(id);
                        form.setFormId(formId);

                        formsToAdd.add(form);
                    }
                }

                this.eventFormRepository.save(formsToAdd);
            }

            transaction.commit();
            transaction.end();
        }
        catch(Exception ex)
        {
            transaction.rollback();
            transaction.end();
        }

        return existingEvent;
    }

    @Override
    public FilterResponse<Map<String, Object>> filter(FilterRequest filterRequest)
    {
        return this.eventRepository.filter(filterRequest);
    }
}
