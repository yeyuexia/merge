package io.github.yeyuexia.merge.notifier;

public class UpdatedField {
  private String path;
  private Object from;
  private Object to;

  public UpdatedField(String path, Object from, Object to) {
    this.path = path;
    this.from = from;
    this.to = to;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Object getFrom() {
    return from;
  }

  public void setFrom(Object from) {
    this.from = from;
  }

  public Object getTo() {
    return to;
  }

  public void setTo(Object to) {
    this.to = to;
  }
}
