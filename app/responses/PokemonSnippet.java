package responses;

import lombok.Getter;
import lombok.Setter;
import models.*;

import java.util.List;

@Getter
@Setter
public class PokemonSnippet
{
    private Long id;
    private String name;
    private Integer number;
    private Region region;
    private Pokemon candyPokemon;
    private Integer candiesToEvolve;

    public PokemonSnippet(Pokemon pokemon)
    {
        this.id = pokemon.getId();
        this.name = pokemon.getName();
        this.number = pokemon.getNumber();
    }
}
