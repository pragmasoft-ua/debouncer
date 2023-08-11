package ua.pragmasoft.template;

public interface Debouncer<T> extends Callback<T> {
  void shutdown();
}