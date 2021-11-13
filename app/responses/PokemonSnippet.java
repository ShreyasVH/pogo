package responses;

import lombok.Getter;
import lombok.Setter;
import models.Pokemon;
import models.Region;
import models.Type;

import java.util.List;

@Getter
@Setter
public class PokemonSnippet
{
    private Long id;
    private String name;
    private Integer number;
    private Region region;
    private List<Type> types;

    public PokemonSnippet(Pokemon pokemon)
    {
        this.id = pokemon.getId();
        this.name = pokemon.getName();
        this.number = pokemon.getNumber();
    }
}
