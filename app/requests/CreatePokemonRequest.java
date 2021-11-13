package requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.ErrorCode;
import exceptions.BadRequestException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePokemonRequest
{
    private String name;
    private Integer number;
    private Long regionId;
    private Integer candiesToEvolve = 0;
    private Long candyPokemonNumber;
    private List<Long> types = new ArrayList<>();
    private List<Long> items = new ArrayList<>();

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

        if(this.regionId == null)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "RegionId cannot be empty");
        }

        if(this.regionId < 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid regionId");
        }

        if(this.types.isEmpty())
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Types cannot be empty");
        }
    }
}
