package no.utdanning.opptak.graphql.dto;

import java.util.List;
import no.utdanning.opptak.domain.Utdanning;

/**
 * Paginert resultat av utdanninger
 */
public class UtdanningPage {
  private List<Utdanning> content;
  private long totalElements;
  private int totalPages;
  private int currentPage;
  private int pageSize;
  private boolean hasNext;
  private boolean hasPrevious;

  public UtdanningPage(
      List<Utdanning> content,
      long totalElements,
      int currentPage,
      int pageSize) {
    this.content = content;
    this.totalElements = totalElements;
    this.currentPage = currentPage;
    this.pageSize = pageSize;
    this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
    this.hasNext = currentPage < totalPages - 1;
    this.hasPrevious = currentPage > 0;
  }

  public List<Utdanning> getContent() {
    return content;
  }

  public void setContent(List<Utdanning> content) {
    this.content = content;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(long totalElements) {
    this.totalElements = totalElements;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public boolean isHasNext() {
    return hasNext;
  }

  public void setHasNext(boolean hasNext) {
    this.hasNext = hasNext;
  }

  public boolean isHasPrevious() {
    return hasPrevious;
  }

  public void setHasPrevious(boolean hasPrevious) {
    this.hasPrevious = hasPrevious;
  }
}