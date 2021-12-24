package responses;

import lombok.Getter;
import lombok.Setter;
import models.Event;
import models.Form;

import java.util.List;

@Getter
@Setter
public class EventSnippet
{
    private Long id;
    private String name;
    private Long startTime;
    private Long endTime;
    private List<Form> forms;

    public EventSnippet(Event event)
    {
        this.id = event.getId();
        this.name = event.getName();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
    }
}
