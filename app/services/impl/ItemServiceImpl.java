package services.impl;

import com.google.inject.Inject;

import java.util.List;
import java.util.concurrent.CompletionStage;

import models.Item;

import repositories.ItemRepository;

import services.ItemService;

public class ItemServiceImpl implements ItemService
{
	private final ItemRepository itemRepository;
	
	@Inject
	public ItemServiceImpl
	(
		ItemRepository itemRepository
	)
	{
		this.itemRepository = itemRepository;
	}

	public CompletionStage<List<Item>> getAll()
	{
		return this.itemRepository.getAll();
	}

    @Override
    public Item get(Long id)
	{
        return this.itemRepository.get(id);
    }
}