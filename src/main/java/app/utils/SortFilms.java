package app.utils;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public enum SortFilms {
    DATE_DESC(Sort.by(Sort.Direction.DESC, "viewDate")),
    DATE_ASC(Sort.by(Sort.Direction.ASC, "viewDate")),
    RATING_DESC(Sort.by(Sort.Direction.DESC, "rating")),
    RATING_ASC(Sort.by(Sort.Direction.ASC, "rating"));

    private final Sort sort;

    SortFilms(Sort sort) {
        this.sort = sort;
    }

}
