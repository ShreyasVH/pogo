package services;

import models.Form;
import requests.forms.CreateFormRequest;
import requests.forms.FilterRequest;
import requests.forms.UpdateRequest;
import responses.FilterResponse;
import responses.FormSnippet;

import java.util.List;

public interface FormService
{
    Form add(CreateFormRequest request);

    FormSnippet get(Long id);

    Form update(UpdateRequest request, Long id);

    FilterResponse filter(FilterRequest request);

}
