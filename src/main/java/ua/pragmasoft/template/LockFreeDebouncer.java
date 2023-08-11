package ua.pragmasoft.template;

import java.time.Clock;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lock free implementation of @see Debouncer uses AtomicLong and
 * IdentityHashMap. Implementation is thread safe, although thread
 * safety was not tested, so use at your own risk or add tests.
 */
public class LockFreeDebouncer implements Debouncer<Runnable> {

  /**
   * Logger.
   */
  static final Logger LOG = LoggerFactory.getLogger(LockFreeDebouncer.class);

  public final long interval;

  private final Clock clock;

  /**
   * Thread safety was not required explicitly neither it is tested, but I still
   * think it's
   * right idea to make implementation thread safe
   */
  private final Map<Runnable, Tracker> hm = Collections.synchronizedMap(new IdentityHashMap<>(2));

  public LockFreeDebouncer(long interval) {
    this.interval = interval;
    this.clock = Clock.systemUTC();
  }

  @Override
  public void call(Runnable r) {
    LOG.debug("call");
    final var t = this.hm.computeIfAbsent(r, unused -> new Tracker());
    t.call(r);
  }

  @Override
  public void shutdown() {
    LOG.info("shutdown");
  }

  final class Tracker implements Callback<Runnable> {
    private final AtomicLong holder = new AtomicLong(0L);

    @Override
    public void call(Runnable r) {
      long now = LockFreeDebouncer.this.clock.millis();
      long last = this.holder
          .getAndUpdate(previous -> now - previous > LockFreeDebouncer.this.interval ? now : previous);
      long remaining = LockFreeDebouncer.this.interval - now + last;
      LOG.debug("remaining {}", remaining);
      if (remaining <= 0) {
        LOG.debug("run");
        r.run();
      }
    }

  }

}
