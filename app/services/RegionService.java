package services;

import java.util.List;

import models.Region;

import java.util.concurrent.CompletionStage;

public interface RegionService
{
	CompletionStage<List<Region>> getAll();
}