package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import requests.events.CreateRequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "events")
@Getter
@Setter
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event extends Model
{
    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private Long startTime;

    @Column
    private Long endTime;

    public Event(CreateRequest request)
    {
        this.name = request.getName();
        this.startTime = request.getStartTime();
        this.endTime = request.getEndTime();
    }
}
