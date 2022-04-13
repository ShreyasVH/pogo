package requests.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateFormRequest
{
    private String name;
    private Integer number;
    private String imageUrl;
    private Long releaseDate;
    private boolean isAlolan = false;
    private boolean isGalarian = false;
    private boolean isHisuian = false;
    private boolean isShiny = false;
    private boolean isFemale = false;
    private boolean isCostumed = false;
    private List<Long> types = new ArrayList<>();

    public void validate()
    {
        if(StringUtils.isEmpty(this.name))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Name cannot be empty");
        }

        if(this.number == null)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Number cannot be empty");
        }

        if(this.number < 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid number");
        }

        if(CollectionUtils.isEmpty(types))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Types");
        }
    }
}
