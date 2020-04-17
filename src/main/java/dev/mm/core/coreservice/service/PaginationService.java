package dev.mm.core.coreservice.service;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import dev.mm.core.coreservice.dto.request.PageRequestDto;
import dev.mm.core.coreservice.dto.response.PageResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Consumer;

@Service
public class PaginationService {

    @Autowired
    private EntityManager entityManager;

    public PageResponseDto getPage(
        PageRequestDto pageRequestDto,
        Consumer<JPAQuery<?>> jpaQueryConsumer
    ) {

        long page = pageRequestDto.getPage() != null ? pageRequestDto.getPage() - 1 : 0L;
        long size = pageRequestDto.getSize() != null ? pageRequestDto.getSize() : 10L;

        JPAQuery jpaQuery = new JPAQuery(entityManager);

        jpaQueryConsumer.accept(jpaQuery);

        jpaQuery.limit(size);
        jpaQuery.offset(page * size);

        QueryResults<?> results = jpaQuery.fetchResults();

        long total = results.getTotal();
        List content = results.getResults();

        PageResponseDto pageResponseDto = new PageResponseDto();
        pageResponseDto.setContent(content);
        pageResponseDto.setNumberOfElements((long) content.size());
        pageResponseDto.setFirst(page == 0);
        pageResponseDto.setNumber(page + 1);
        pageResponseDto.setSize(size);
        pageResponseDto.setTotalElements(total);
        pageResponseDto.setTotalPages((total / size) + (total % size == 0 ? 0 : 1));
        pageResponseDto.setLast(page + 1 == pageResponseDto.getTotalPages());

        return pageResponseDto;
    }
}
