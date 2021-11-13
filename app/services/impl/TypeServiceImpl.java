package services.impl;

import com.google.inject.Inject;

import java.util.List;
import java.util.concurrent.CompletionStage;

import models.Type;

import repositories.TypeRepository;

import services.TypeService;

public class TypeServiceImpl implements TypeService
{
	private final TypeRepository typeRepository;
	
	@Inject
	public TypeServiceImpl
	(
		TypeRepository typeRepository
	)
	{
		this.typeRepository = typeRepository;
	}

	public CompletionStage<List<Type>> getAll()
	{
		return this.typeRepository.getAll();
	}
}