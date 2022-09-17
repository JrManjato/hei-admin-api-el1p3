package school.hei.haapi.endpoint.rest.mapper;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.hei.haapi.endpoint.rest.model.CreateEventParticipants;
import school.hei.haapi.endpoint.rest.model.EventParticipant;
import school.hei.haapi.model.BoundedPageSize;
import school.hei.haapi.model.Event;
import school.hei.haapi.model.PageFromOne;
import school.hei.haapi.model.User;
import school.hei.haapi.service.EventService;
import school.hei.haapi.service.UserService;

@Component
@AllArgsConstructor
public class EventParticipantMapper {
  private final UserService userService;
  private final EventService eventService;

  public EventParticipant toRest(school.hei.haapi.model.EventParticipant domain) {
    return new EventParticipant()
        .id(domain.getId())
        .ref(domain.getUser().getId())
        .eventId(domain.getEvent().getId())
        .status(EventParticipant.StatusEnum.fromValue(domain.getStatus().toString()));
  }

  public List<school.hei.haapi.model.EventParticipant> toDomain(CreateEventParticipants toCreate) {
    final int MAX_PAGE = 500;
    List<school.hei.haapi.model.EventParticipant> participants = new ArrayList<>();
    List<User> students =
        userService.getByGroup(new PageFromOne(1), new BoundedPageSize(500),
            User.Role.STUDENT, toCreate.getGroupId());

    Event event = eventService.getById(toCreate.getEventId());
    for (User student : students) {
      participants.add(
          school.hei.haapi.model.EventParticipant
              .builder()
              .id(student.getId())
              .user(student)
              .event(event)
              .status(school.hei.haapi.model.EventParticipant.StatusEnum.EXPECTED)
              .build()
      );
    }
    return participants;
  }

  public List<school.hei.haapi.model.EventParticipant> toSingleList(
      List<List<school.hei.haapi.model.EventParticipant>> toListify) {
    List<school.hei.haapi.model.EventParticipant> participants = new ArrayList<>();
    for (List<school.hei.haapi.model.EventParticipant> eventParticipants : toListify) {
      participants.addAll(eventParticipants);
    }
    return participants;
  }
}
