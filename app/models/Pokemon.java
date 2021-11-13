package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;

import javax.persistence.*;

import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import requests.CreatePokemonRequest;

@Entity
@Table(name = "pokemons")
@Getter
@Setter
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pokemon extends Model
{
    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private Integer number;

    @Column
    private Long regionId;

    @Column
    private Integer candiesToEvolve;

    public Pokemon(CreatePokemonRequest request)
    {
        this.name = request.getName();
        this.number = request.getNumber();
        this.regionId = request.getRegionId();
        this.candiesToEvolve = request.getCandiesToEvolve();
    }
}