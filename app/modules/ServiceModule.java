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
	}
}