package requests.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateFormRequest
{
    private String name;
    private Integer number;
    private String imageUrl;
    private Long releaseDate;
//    private boolean isAlolan;

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
    }
}
