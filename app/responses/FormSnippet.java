package responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.Form;
import models.Pokemon;
import models.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormSnippet
{
    private Long id;
    private String name;
    private String pokemonName;
    private Integer pokemonNumber;
    private Long regionId;
    private String regionName;
    private String imageUrl;
    private Long releaseDate;
    private boolean isAlolan;
    private boolean isGalarian;
    private boolean isHisuian;
    private boolean isShiny;
    private boolean isFemale;
    private boolean isCostumed;
    private List<Long> typeIds = new ArrayList<>();
    private List<String> typeNames = new ArrayList<>();

    public FormSnippet(Form form, List<Type> types)
    {
        this.id = form.getId();
        this.name = form.getName();
        this.imageUrl = form.getImageUrl();
        this.releaseDate = form.getReleaseDate();
        this.typeIds = types.stream().map(Type::getId).collect(Collectors.toList());
        this.typeNames = types.stream().map(Type::getName).collect(Collectors.toList());
        this.isAlolan = form.isAlolan();
        this.isGalarian = form.isGalarian();
        this.isHisuian = form.isHisuian();
        this.isShiny = form.isShiny();
        this.isFemale = form.isFemale();
        this.isCostumed = form.isCostumed();
    }
}
