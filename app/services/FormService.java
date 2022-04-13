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
    FormSnippet add(CreateFormRequest request);

    FormSnippet get(Long id);

    FormSnippet update(UpdateRequest request, Long id);

    FilterResponse<FormSnippet> filter(FilterRequest request);

}
