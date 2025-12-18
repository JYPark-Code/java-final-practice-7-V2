package attendance.repository;

import attendance.domain.crew.Crews;

public interface CrewRepository {
    Crews load();
}
