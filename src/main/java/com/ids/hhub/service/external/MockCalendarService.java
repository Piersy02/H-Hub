package com.ids.hhub.service.external;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service("mockCalendar")
public class MockCalendarService implements CalendarService {

    @Override
    public String scheduleMeeting(String mentorEmail, String teamLeaderEmail, String dateTime) {
        System.out.println("[MOCK CALENDAR] Prenotazione da " + mentorEmail + " per " + teamLeaderEmail + " alle " + dateTime);
        return "https://meet.fake-calendar.com/" + UUID.randomUUID().toString();
    }
}
