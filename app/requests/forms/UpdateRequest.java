package requests.forms;

import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class UpdateRequest
{
    private String name;
    private Integer number;
    private String imageUrl;
    private Long releaseDate;

    public void validate()
    {
        if(this.number != null && this.number < 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid number");
        }
    }
}
