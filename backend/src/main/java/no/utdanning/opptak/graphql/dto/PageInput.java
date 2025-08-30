package no.utdanning.opptak.graphql.dto;

/**
 * Input for paginering
 */
public class PageInput {
  private Integer size = 20;
  private Integer page = 0;

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    if (size != null && size > 0 && size <= 100) {
      this.size = size;
    }
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    if (page != null && page >= 0) {
      this.page = page;
    }
  }
}