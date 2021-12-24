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
public class UpdatePokemonRequest
{
    private String name;
    private Integer number;
    private Long regionId;
    private Integer candiesToEvolve = 0;
    private Integer candyPokemonNumber;

    public void validate()
    {
        if(this.number != null && this.number <= 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid number");
        }

        if(this.regionId != null && this.regionId <= 0)
        {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid regionId");
        }
    }
}
