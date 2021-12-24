package modules;

import services.*;
import services.impl.*;

import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule
{
	@Override
	public void configure()
	{
		bind(TypeService.class).to(TypeServiceImpl.class).asEagerSingleton();
		bind(RegionService.class).to(RegionServiceImpl.class).asEagerSingleton();
		bind(ItemService.class).to(ItemServiceImpl.class).asEagerSingleton();
		bind(PokemonService.class).to(PokemonServiceImpl.class).asEagerSingleton();
		bind(FormService.class).to(FormServiceImpl.class).asEagerSingleton();
	}
}