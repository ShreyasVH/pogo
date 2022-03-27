package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;

import javax.persistence.*;

import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import requests.forms.CreateFormRequest;

@Entity
@Table(name = "forms")
@Getter
@Setter
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Form extends Model
{
    @Id
    @Column
    private Long id;

    @Column
    private Integer pokemonNumber;

    @Column
    private String name;

    @Column
    private String imageUrl;

    @Column
    private Long releaseDate;

    @Column
    private boolean isAlolan;

    @Column
    private boolean isGalarian;

    @Column
    private boolean isHisuian;

//    @Column
//    private boolean isShadow;
//
    @Column
    private boolean isShiny;

    @Column
    private boolean isFemale;

    @Column
    private boolean isCostumed;

    @Column
    private boolean isReleased;

    public Form(CreateFormRequest request)
    {
        this.name = request.getName();
        this.imageUrl = request.getImageUrl();
        this.releaseDate = request.getReleaseDate();
        this.pokemonNumber = request.getNumber();
        this.isCostumed = request.isCostumed();
        this.isAlolan = request.isAlolan();
        this.isGalarian = request.isGalarian();
        this.isHisuian = request.isHisuian();
        this.isFemale = request.isFemale();
        this.isShiny = request.isShiny();
    }
}