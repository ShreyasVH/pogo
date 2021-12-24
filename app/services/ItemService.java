package services;

import java.util.List;

import models.Item;

import java.util.concurrent.CompletionStage;

public interface ItemService
{
	CompletionStage<List<Item>> getAll();

	Item get(Long id);
}