package dev.mvvasilev.common.data;

import java.time.LocalDateTime;

public interface DatabaseStorable {

    long getId();

    LocalDateTime getTimeCreated();

    LocalDateTime getTimeLastModified();

}
