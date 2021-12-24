package responses;

import lombok.Getter;
import lombok.Setter;
import models.Form;
import models.Pokemon;

@Getter
@Setter
public class FormSnippet
{
    private Long id;
    private Pokemon pokemon;
    private String name;
    private String imageUrl;
    private Long releaseDate;

    public FormSnippet(Form form)
    {
        this.id = form.getId();
        this.name = form.getName();
        this.imageUrl = form.getImageUrl();
        this.releaseDate = form.getReleaseDate();
    }
}
