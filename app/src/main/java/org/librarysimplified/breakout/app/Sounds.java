package org.librarysimplified.breakout.app;

import android.content.Context;
import android.media.SoundPool;

public final class Sounds {

  private final SoundPool soundPool;

  private final int blockBreak;
  private final int tak;
  private final int bounce;
  private final int failure;
  private final int ready;
  private final int go;

  Sounds(Context context) {
    this.soundPool =
      new SoundPool.Builder()
        .setMaxStreams(3)
        .build();

    this.blockBreak =
      this.soundPool.load(context, R.raw.blockbreak, 1);
    this.tak =
      this.soundPool.load(context, R.raw.tak, 1);
    this.bounce =
      this.soundPool.load(context, R.raw.bounce, 1);
    this.failure =
      this.soundPool.load(context, R.raw.failure, 1);
    this.ready =
      this.soundPool.load(context, R.raw.ready, 1);
    this.go =
      this.soundPool.load(context, R.raw.go, 1);
  }

  public int getFailure() {
    return failure;
  }

  public int getReady() {
    return ready;
  }

  public int getGo() {
    return go;
  }

  void release() {
    this.soundPool.release();
  }

  public int getBlockBreak() {
    return blockBreak;
  }

  public int getTak() {
    return tak;
  }

  public int getBounce() {
    return bounce;
  }

  void playRandomRate(int sound) {
    final double rate = (Math.random() * 1.25) + 0.75;
    play(sound, rate);
  }

  void play(
    int sound,
    double rate) {
    this.soundPool.play(sound, 0.5f, 0.5f, 1, 0, (float) rate);
  }
}
