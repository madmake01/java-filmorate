package ru.yandex.practicum.filmorate.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import java.sql.SQLException;
import java.util.function.Supplier;

@Component
public class ConstraintExceptionHandler {

    private static final String FK_VIOLATION_SQL_STATE = "23506";

    public <T> T handleForeignKeyViolation(Supplier<T> action, String notFoundMessage) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            SQLException sqlEx = getSqlException(ex);
            if (sqlEx != null && FK_VIOLATION_SQL_STATE.equals(sqlEx.getSQLState())) {
                throw new EntityNotFoundException(notFoundMessage, ex);
            }
            throw ex;
        }
    }

    private SQLException getSqlException(Throwable ex) {
        while (ex != null) {
            if (ex instanceof SQLException sqlEx) {
                return sqlEx;
            }
            ex = ex.getCause();
        }
        return null;
    }
}
