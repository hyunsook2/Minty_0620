package com.Reboot.Minty.job.repository;

import com.Reboot.Minty.job.dto.JobDto;
import com.Reboot.Minty.job.dto.JobSearchDto;
import com.Reboot.Minty.job.dto.QJobDto;
import com.Reboot.Minty.job.entity.QJob;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Repository
public class JobCustomRepository {
    private final JPAQueryFactory queryFactory;

    public JobCustomRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.equals("title", searchBy)) {
            return QJob.job.title.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("jobLocation", searchBy)) {
            return QJob.job.jobLocation.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("payTotal", searchBy)) {
            return QJob.job.payTotal.like("%" + searchQuery + "%");
        }
        return null;
    }

    public Page<JobDto> findJobsBySearchDto(JobSearchDto jobSearchDto, Pageable pageable) {
        QJob qJob = QJob.job;
        List<JobDto> jobs = queryFactory.select(new QJobDto(
                        qJob.id, qJob.title,
                        qJob.createdDate, qJob.jobLocation,
                        qJob.payTotal, qJob.thumbnail))
                .from(qJob).where(searchByLike(jobSearchDto.getSearchBy(), jobSearchDto.getSearchQuery())).orderBy(qJob.createdDate.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        long total = queryFactory.select(Wildcard.count).from(qJob).where(searchByLike(jobSearchDto.getSearchBy(), jobSearchDto.getSearchQuery())).fetchOne();

        return new PageImpl<>(jobs, pageable, total);
    }

}