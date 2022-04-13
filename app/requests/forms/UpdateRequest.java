package requests.forms;

import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpdateRequest
{
    private String name;
    private Integer number;
    private String imageUrl;
    private Long releaseDate;
    private Boolean isAlolan;
    private Boolean isGalarian;
    private Boolean isHisuian;
    private Boolean isShiny;
    private Boolean isFemale;
    private Boolean isCostumed;
    private List<Long> types;

    public void validate()
    {
        if(this.number != null && this.number < 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid number");
        }

        if(this.types != null && this.types.isEmpty())
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Types cannot be empty");
        }
    }
}
