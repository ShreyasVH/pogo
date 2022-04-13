package services;

import java.util.List;

import models.Region;

import java.util.concurrent.CompletionStage;

public interface RegionService
{
	List<Region> getAll();

	Region get(Long id);
}