package requests.events;

import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateRequest
{
    private String name;
    private Long startTime;
    private Long endTime;
    private List<Long> forms = new ArrayList<>();

    public void validate()
    {
        if(StringUtils.isEmpty(this.name))
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Name cannot be empty");
        }

        if(this.startTime == null || this.startTime <= 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid start time");
        }

        if(this.endTime == null || this.endTime <= 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid end time");
        }

        if(this.forms == null || this.forms.isEmpty())
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Forms cannot be empty");
        }
    }
}
