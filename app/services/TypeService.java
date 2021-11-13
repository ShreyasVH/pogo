package services;

import java.util.List;

import models.Type;

import java.util.concurrent.CompletionStage;

public interface TypeService
{
	CompletionStage<List<Type>> getAll();
}