package responses;

import lombok.Getter;
import lombok.Setter;
import models.Form;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FilterResponse<T>
{
    private Integer offset = 0;
    private Long totalCount = 0L;
    private List<T> list = new ArrayList<>();
}
