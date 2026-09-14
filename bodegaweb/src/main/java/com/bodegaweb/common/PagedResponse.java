package com.bodegaweb.common;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Representación estable (serializable) de una página de resultados,
 * para no exponer directamente {@link org.springframework.data.domain.PageImpl}.
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
