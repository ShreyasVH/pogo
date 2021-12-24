package services.impl;

import com.google.inject.Inject;

import java.util.List;
import java.util.concurrent.CompletionStage;

import models.Region;

import repositories.RegionRepository;

import services.RegionService;

public class RegionServiceImpl implements RegionService
{
	private final RegionRepository regionRepository;
	
	@Inject
	public RegionServiceImpl
	(
		RegionRepository regionRepository
	)
	{
		this.regionRepository = regionRepository;
	}

	public CompletionStage<List<Region>> getAll()
	{
		return this.regionRepository.getAll();
	}

	@Override
	public Region get(Long id)
	{
		return this.regionRepository.get(id);
	}
}