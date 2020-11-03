package io.github.r2d2project.core.dao.implement;

import io.github.r2d2project.core.dao.LogReadDao;
import io.github.r2d2project.core.dto.api.logs.Data;
import io.github.r2d2project.core.dto.api.logs.GetRequest;
import io.github.r2d2project.core.dto.api.logs.GetResponse;
import io.github.r2d2project.core.dto.api.logs.Item;
import io.github.r2d2project.core.mysql.po.LogPo;
import io.github.r2d2project.core.mysql.repository.LogJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service("LogReadJpaDao")
public class LogReadJpaDao implements LogReadDao {
  private static class Filter implements Specification<LogPo> {
    GetRequest request;

    @Override
    public Predicate toPredicate(
        Root<LogPo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
      Long idGreaterThan = request.getIdGreaterThan();
      Long idLessThan = request.getIdLessThan();
      String instanceContain = request.getInstanceContain();
      String levelContain = request.getLevelContain();
      String categoryContain = request.getCategoryContain();
      Long timestampMsGreaterThan = request.getTimestampMsGreaterThan();
      Long timestampMsLessThan = request.getTimestampMsLessThan();
      String contentContain = request.getContentContain();
      List<Predicate> predicateList = new ArrayList<>();
      if (idGreaterThan != null) {
        predicateList.add(
            criteriaBuilder.greaterThan(root.get("id").as(Long.class), idGreaterThan));
      }
      if (idLessThan != null) {
        predicateList.add(criteriaBuilder.lessThan(root.get("id").as(Long.class), idLessThan));
      }
      if (instanceContain != null) {
        predicateList.add(
            criteriaBuilder.like(
                root.get("instance").as(String.class), String.format("%%%s%%", instanceContain)));
      }
      if (levelContain != null) {
        predicateList.add(
            criteriaBuilder.like(
                root.get("level").as(String.class), String.format("%%%s%%", levelContain)));
      }
      if (categoryContain != null) {
        predicateList.add(
            criteriaBuilder.like(
                root.get("category").as(String.class), String.format("%%%s%%", categoryContain)));
      }
      if (timestampMsGreaterThan != null) {
        predicateList.add(
            criteriaBuilder.greaterThan(
                root.get("timestampMs").as(Long.class), timestampMsGreaterThan));
      }
      if (timestampMsLessThan != null) {
        predicateList.add(
            criteriaBuilder.lessThan(root.get("timestampMs").as(Long.class), timestampMsLessThan));
      }
      if (contentContain != null) {
        predicateList.add(
            criteriaBuilder.like(
                root.get("content").as(String.class), String.format("%%%s%%", contentContain)));
      }
      Predicate[] array = new Predicate[request.getStatementNumber()];
      return criteriaBuilder.and(predicateList.toArray(array));
    }

    public Filter(GetRequest request) {
      this.request = request;
    }
  }

  private final LogJpaRepository repository;

  @Autowired
  public LogReadJpaDao(LogJpaRepository repository) {
    this.repository = repository;
  }

  public GetResponse readAdvance(GetRequest request) {
    // database
    Page<LogPo> page =
        repository.findAll(
            new Filter(request),
            PageRequest.of(
                (int) request.getPage() - 1,
                (int) request.getSize(),
                Sort.by(
                    request.getAscending() ? Sort.Direction.ASC : Sort.Direction.DESC,
                    request.getOrder())));
    // response
    GetResponse response = new GetResponse();
    Data data = response.getData();
    List<Item> logList = data.getLogList();
    Item log;
    for (LogPo po : page) {
      log = new Item();
      log.setId(po.getId());
      log.setInstance(po.getInstance());
      log.setCategory(po.getCategory());
      log.setLevel(po.getLevel());
      log.setTimestampMs(po.getTimestampMs());
      log.setContent(po.getContent());
      logList.add(log);
    }
    data.setPage(page.getNumber() + 1L);
    data.setSize((long) page.getSize());
    data.setTotal(page.getTotalElements());
    data.setTotalPage((long) page.getTotalPages());
    data.setPrevious(page.isFirst() ? "" : request.generatePreviousUrl());
    data.setNext(page.isLast() ? "" : request.generateNextUrl());
    return response;
  }
}
